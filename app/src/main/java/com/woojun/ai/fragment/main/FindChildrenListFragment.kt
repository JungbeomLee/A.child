package com.woojun.ai.fragment.main

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.woojun.ai.MainActivity
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentFindChildrenListBinding
import com.woojun.ai.util.AppDatabase
import com.woojun.ai.util.ChildInfo
import com.woojun.ai.util.MyChildAdapterType
import com.woojun.ai.util.MyChildInfoAdapter
import com.woojun.ai.util.ProgressUtil.createDialog
import com.woojun.ai.util.ProgressUtil.createLoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FindChildrenListFragment : Fragment() {

    private var _binding: FragmentFindChildrenListBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val list = arrayListOf<ChildInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindChildrenListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference

        binding.apply {

            val (loadingDialog, setDialogText) = createLoadingDialog(requireContext())
            loadingDialog.show()
            setDialogText("로딩중")

            CoroutineScope(Dispatchers.IO).launch {

                val db = AppDatabase.getDatabase(requireContext())
                val findChildDao = db!!.findChildDao()

                val similarDistanceUid = findChildDao.getFindChild().similarDistanceUid
                withContext(Dispatchers.Main) {
                    setDialogText("유사한 아이들을 검색중")
                    similarDistanceUid.forEach {
                        database.child("children").child(it[0].replace("\"", "")).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val value = snapshot.getValue(ChildInfo::class.java)

                                    if (list.size == similarDistanceUid.size) {
                                        setDialogText("검색 완료!")
                                        Handler().postDelayed({
                                            loadingDialog.dismiss()

                                            findChildrenList.layoutManager = LinearLayoutManager(requireContext().applicationContext)
                                            findChildrenList.adapter = MyChildInfoAdapter(list, MyChildAdapterType.Find)
                                        }, 500)
                                    } else {
                                        list.add(value!!)
                                        if (list.size == similarDistanceUid.size) {
                                            setDialogText("검색 완료!")
                                            Handler().postDelayed({
                                                loadingDialog.dismiss()

                                                findChildrenList.layoutManager = LinearLayoutManager(requireContext().applicationContext)
                                                findChildrenList.adapter = MyChildInfoAdapter(list, MyChildAdapterType.Find)
                                            }, 500)
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                setDialogText("검색 실패")
                                loadingDialog.dismiss()
                                createDialog(
                                    requireContext(),
                                    false,
                                    "유사한 아동 검색 실패",
                                    "사진과 유사한 아동 검색을 실패했습니다\n" +
                                            "다시 한번 아동 얼굴을 촬영해주시고\n" +
                                            "만약 오류가 반복된다면 제보부탁드립니다"
                                ) { dialog ->
                                    dialog.dismiss()
                                    val mainActivity = activity as MainActivity
                                    mainActivity.hideBottomNavigation(false)
                                    view.findNavController().popBackStack(R.id.action_findChildrenListFragment_to_home2, false)
                                }
                            }
                        })
                    }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}