package com.woojun.ai.fragment.main

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.woojun.ai.MainActivity
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentCameraBinding
import com.woojun.ai.util.AppDatabase
import com.woojun.ai.util.CameraType
import com.woojun.ai.util.ChildInfo
import com.woojun.ai.util.FindChildImageResult
import com.woojun.ai.util.ProgressUtil.createDialog
import com.woojun.ai.util.ProgressUtil.createLoadingDialog
import com.woojun.ai.util.RetrofitAPI
import com.woojun.ai.util.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val (loadingDialog, setDialogText) = createLoadingDialog(requireContext())

        binding.apply {

            val bundle = arguments // 번들
            val type = bundle?.getParcelable<CameraType>("camera type") // 이전 화면에서 넘어온 CameraType
            val childInfo = bundle?.getParcelable<ChildInfo>("child info") // 이전 화면에서 넘어온 ChildInfo

            if (type == CameraType.Find) { // 만약 이전 화면에서 넘어온 CameraType이 Find 라면
                val mainActivity = activity as MainActivity
                mainActivity.hideBottomNavigation(true) // BottomNavigation 숨기기
            }

            camera.setLifecycleOwner(this@CameraFragment) // cameraView 기본 설정

            camera.addCameraListener(object : CameraListener() {
                override fun onPictureTaken(result: PictureResult) { // 카메라 촬영 시 호출
                    camera.visibility = View.INVISIBLE // cameraView 숨기기
                    captureBtn.visibility = View.INVISIBLE // 카메라 버튼 숨기기
                    image.visibility = View.INVISIBLE // 가이드 이미지 숨기기

                    loadingDialog.show() // 로딩 Dialog 보여주기
                    setDialogText("사진 저장중")

                    result.toBitmap { bitmap -> // 사진 결과 값을 bitmap으로 바꾸고
                        if (bitmap != null) { // 만약 bitmap이 null이 아니라면
                            CoroutineScope(Dispatchers.IO).launch { // IO 스레드에서 코드 실행
                                val file = File(
                                    context?.filesDir,
                                    "${childInfo?.name ?: "${System.currentTimeMillis()}"}.jpg"
                                ) // 앱 저장소 저장할 파일을 만든다, 파일명을 childInfo에서 아이 이름을 사용하거나 없다면(Find 경우) 현재 시각으로

                                try { // 혹시 모를 오류처리를 위해 try 문

                                    // 사진을 JPEG 형식으로 만든다
                                    val fileOutputStream = FileOutputStream(file)
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                                    fileOutputStream.close()

                                    withContext(Dispatchers.Main) { // 메인 스레드에서 실행
                                        if (type == CameraType.ChildRegister) { // 만약 type이 ChildRegister라면 (아이 등록)
                                            val storageRef = FirebaseStorage.getInstance().reference // FirebaseStorage 세팅
                                            val imageRef = storageRef.child("child_images/${childInfo!!.name}.jpg") // Storage 경로
                                            setDialogText("이미지 업로드 중")

                                            imageRef.putBytes(bitmapToByteArray(bitmap)) // imageRef에 ByteArray 형태로 이미지를 저장함
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) { // 만약 결과가 성공이라면
                                                        imageRef.downloadUrl.addOnSuccessListener { uri -> // 저장한 이미지 주소를 가져와서

                                                            childInfo.photo = uri.toString() // childInfo photo 주소를 꾼다

                                                            // 레트로핏 기본 세팅
                                                            val retrofit = RetrofitClient.getInstance()
                                                            val apiService = retrofit.create(RetrofitAPI::class.java)

                                                            // POST 요청에 이미지를 담는 과정
                                                            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                                                            val multipartBody = MultipartBody.Part.createFormData("FixImage", file.name, requestFile)

                                                            val call = apiService.setChildImage(multipartBody, childInfo.id)
                                                            setDialogText("아이 정보 등록중")
                                                            // POST 요청 보내기
                                                            call.enqueue(object : Callback<String> {
                                                                override fun onResponse(call: Call<String>, response: Response<String>) {
                                                                    loadingDialog.dismiss() // 로딩 Dialog 없애기
                                                                    if (response.isSuccessful && response.body() == "success") { // 만약 성공적으로 요청을 보내고 success가 뜨면
                                                                        updateUserChildInfo(childInfo) // updateUserChildInfo 함수 실행
                                                                    } else { // 만약 요청을 보내는 걸 실패했다면
                                                                        showFailDialog(CameraType.ChildRegister) // showFailDialog 함수 실행
                                                                    }
                                                                }

                                                                override fun onFailure(call: Call<String>, t: Throwable) { // 요청 보내기에 실패했다면
                                                                    setDialogText("아이 등록 실패")
                                                                    Handler().postDelayed({
                                                                        loadingDialog.dismiss() // 로딩 Dialog 없애기
                                                                        showFailDialog(CameraType.ChildRegister) // showFailDialog 함수 실행
                                                                    }, 500)
                                                                }
                                                            })

                                                        }.addOnFailureListener { // 이미지 주소를 가져오지 못했다면
                                                            setDialogText("이미지 가져오기 실패")
                                                            Handler().postDelayed({
                                                                loadingDialog.dismiss() // 로딩 Dialog 없애기
                                                                showFailDialog(CameraType.ChildRegister) // showFailDialog 함수 실행
                                                            }, 500)
                                                        }
                                                    } else { // Storage에 이미지를 저장하지 못했다면
                                                        setDialogText("이미지 업로드 실패")
                                                        Handler().postDelayed({
                                                            loadingDialog.dismiss() // 로딩 Dialog 없애기
                                                            showFailDialog(CameraType.ChildRegister) // showFailDialog 함수 실행
                                                        }, 500)
                                                    }
                                                }

                                        }
                                        else { // 만약 type이 Find라면 (아이 조회)

                                            setDialogText("아이 조회중")

                                            // 레트로핏 기본 세팅
                                            val retrofit = RetrofitClient.getInstance()
                                            val apiService = retrofit.create(RetrofitAPI::class.java)

                                            // POST 요청에 이미지를 담는 과정
                                            val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                                            val multipartBody = MultipartBody.Part.createFormData("FixImage", file.name, requestFile)

                                            val call = apiService.findChildImage(multipartBody)

                                            // POST 요청 보내기
                                            call.enqueue(object : Callback<FindChildImageResult> {
                                                override fun onResponse(call: Call<FindChildImageResult>, response: Response<FindChildImageResult>) {
                                                    loadingDialog.dismiss() // 로딩 Dialog 없애기
                                                    if (response.isSuccessful) { // 만약 요청이 성공적이라면
                                                        CoroutineScope(Dispatchers.IO).launch { // db에 uid 저장
                                                            val db = AppDatabase.getDatabase(requireContext())
                                                            val findChildDao = db!!.findChildDao()
                                                            val findChild = findChildDao.getFindChild()

                                                            findChild.similarDistanceUid = response.body()!!.similar_distance_uid.distinct().reversed()

                                                            findChildDao.updateFindChild(findChild)
                                                        }
                                                        // findListFragment로 이동함
                                                        view.findNavController().navigate(R.id.action_cameraFragment_to_findChildrenListFragment)
                                                    } else { // 실패했다면
                                                        setDialogText("아이 조회 실패")
                                                        Handler().postDelayed({
                                                            showFailDialog(CameraType.Find) // showFailDialog 함수 실행
                                                        }, 500)
                                                    }
                                                }

                                                override fun onFailure(call: Call<FindChildImageResult>, t: Throwable) {
                                                    setDialogText("아이 조회 실패")
                                                    Handler().postDelayed({
                                                        loadingDialog.dismiss()
                                                        showFailDialog(CameraType.Find) // showFailDialog 함수 실행
                                                    }, 500)
                                                }
                                            })
                                        }
                                    }
                                }
                                catch (e: IOException) { // try 문에서 발생한 오류 처리
                                    withContext(Dispatchers.Main) {
                                        // 메인 스레드에서 실행
                                        setDialogText("사진 실패")
                                        Handler().postDelayed({
                                            loadingDialog.dismiss() // 로딩 Dialog 없애기
                                            if (type == CameraType.ChildRegister) { // 만약 type이 ChildRegister라면 (아이 등록)
                                                showFailDialog(CameraType.ChildRegister) // showFailDialog 실행
                                            } else { // 만약 type이 Find라면 (아이 조회)
                                                showFailDialog(CameraType.Find) // showFailDialog 실행
                                            }
                                        }, 500)
                                    }
                                }
                            }
                        } else {
                            setDialogText("사진 저장 실패")
                            Handler().postDelayed({
                                loadingDialog.dismiss()
                            },500)
                        }
                    }
                }

                override fun onVideoTaken(result: VideoResult) {
                }

                override fun onVideoRecordingEnd() {
                }

                override fun onPictureShutter() {
                }

                override fun onVideoRecordingStart() {
                }
            })

            captureBtn.setOnClickListener { // 카메라 버튼 클릭 시
                camera.takePicture() // 촬영
            }

            changeButton.setOnClickListener {
                camera.toggleFacing() // 전면 후면 카메라 촬영
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateUserChildInfo(childInfo: ChildInfo) {
        database = Firebase.database.reference // realtimeDatabase 세팅

        CoroutineScope(Dispatchers.IO).launch {// IO 스레드에서
            val db = AppDatabase.getDatabase(requireContext()) // 데이터베이스 호출
            val userDao = db!!.userInfoDao() // userDao 가져오기
            val user = userDao.getUser() // user 정보 가져오기

            var isNotUpdate = true // 정보 수정인지 처음 등록인지 체크

            user.children.forEachIndexed { index, it ->
                if (it.id == childInfo.id) { // 정보 수정이라면
                    isNotUpdate = false // 체크 변수 false
                    user.children[index] = childInfo // 업데이트
                }
            }

            if (isNotUpdate) { // 처음 등록이라면
                user.children.add(childInfo) // 아이 추가
            }

            userDao.updateUser(user) // 결과 반영
            database.child("users").child("${auth.uid}").setValue(user) // Realtime Database 유저 정보 업데이드
            database.child("children").child(childInfo.id).setValue(childInfo) // Realtime Database 아동 정보 업데이드
        }

        createDialog( // 결과 Dialog 보여주기
            requireContext(),
            true,
            "우리 아이 신원등록 성공",
            "우리 아이의 신원을 성공적으로 등록했습니다\n" +
                    "아이 인적사항에 변경사항이 생겼다면\n" +
                    "수정 탭에서 수정을 눌러주세요"
        ) {
            it.dismiss()
            val mainActivity = activity as MainActivity
            mainActivity.hideBottomNavigation(false) // 다시 Bottom Navigation을 보여주고
            view?.findNavController()?.navigate(R.id.action_cameraFragment_to_home) // homeFragment로 이동
        }

    }

    fun bitmapToByteArray(bitmap: Bitmap): ByteArray { // Bitmap을 ByteArray로 변경
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    fun showFailDialog(type: CameraType) { // 실패시 type에 맞게 실패 Dialog 보여줌
        binding.apply {
            if (type == CameraType.ChildRegister) {
                createDialog(
                    requireContext(),
                    false,
                    "우리 아이 신원등록 실패",
                    "우리 아이의 신원등록을 실패했습니다\n" +
                            "다시 얼굴을 정확히 가이드라인에 맞춰 시도해주세요\n" +
                            "만약 계속해서 오류가 발생한다면 제보 부탁드립니다"
                ) {
                    it.dismiss()
                    camera.visibility = View.VISIBLE // cameraView 보여주기
                    captureBtn.visibility = View.VISIBLE // 카메라 버튼 보여주기
                    image.visibility = View.VISIBLE // 가이드 이미지 보여주기

                    loadingDialog.dismiss() // 로딩 Dialog 없애기
                }
            } else {
                createDialog(
                    requireContext(),
                    false,
                    "실종아동 신원조회 실패",
                    "실종아동 신원조회를 실패했습니다\n" +
                            "다시 얼굴을 정확히 가이드라인에 맞춰 시도해주세요\n" +
                            "만약 계속해서 오류가 발생한다면 제보 부탁드립니다"
                ) {
                    it.dismiss()
                    camera.visibility = View.VISIBLE // cameraView 보여주기
                    captureBtn.visibility = View.VISIBLE // 카메라 버튼 보여주기
                    image.visibility = View.VISIBLE // 가이드 이미지 보여주기

                    loadingDialog.dismiss() // 로딩 Dialog 없애기
                }
            }
        }
    }

}