package com.woojun.ai.fragment.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.woojun.ai.MainActivity
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentHomeBinding
import com.woojun.ai.util.AppDatabase
import com.woojun.ai.util.CameraType
import com.woojun.ai.util.ChildInfoType
import com.woojun.ai.util.ChildrenInfoAdapter
import com.woojun.ai.util.DpToPxUtil
import com.woojun.ai.util.FilterOptionUtil.filterAdults
import com.woojun.ai.util.FilterOptionUtil.filterChildren
import com.woojun.ai.util.FilterOptionUtil.getSelectedItem
import com.woojun.ai.util.FilterOptionUtil.showPopupMenu
import com.woojun.ai.util.ProgressUtil
import com.woojun.ai.util.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = Firebase.database.reference
        auth = Firebase.auth

        binding.apply {

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(requireContext())
                val user = db!!.userInfoDao().getUser()

                withContext(Dispatchers.Main) {
                    if (user.policeCheck) {
                        childrenIdentificationButton.visibility = View.VISIBLE
                    } else {
                        childrenIdentificationButton.visibility = View.GONE
                    }
                    helloUserText.text = "반갑습니다 ${user.name}님"

                    if (user.children.size == 0) {
                        childProfileBox.visibility = View.GONE
                        childNameText.visibility = View.GONE
                        childSexAndBirthText.visibility = View.GONE
                        childInfoSideBar.visibility = View.GONE

                        childRegistrationText.visibility = View.VISIBLE
                    } else {
                        childRegistrationText.visibility = View.GONE

                        childProfileBox.visibility = View.VISIBLE
                        childNameText.visibility = View.VISIBLE
                        childSexAndBirthText.visibility = View.VISIBLE
                        childInfoSideBar.visibility = View.VISIBLE

                        Glide.with(requireContext())
                            .load(user.children[0].photo)
                            .error(R.drawable.child)
                            .apply(RequestOptions.circleCropTransform())
                            .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                            .format(DecodeFormat.PREFER_RGB_565)
                            .thumbnail(0.5f)
                            .override(DpToPxUtil.dpToPx(binding.root.context, 52f))
                            .into(childProfile)

                        childNameText.text = user.children[0].name
                        childSexAndBirthText.text = "${user.children[0].sex} · ${user.children[0].birthDate.substring(0 until 4)}년생"
                        lastIdentityDate.text = formatDate(user.children[0].lastIdentityDate)
                        nextIdentityDate.text = nextIdentityDateFormat(user.children[0].lastIdentityDate)

                        if (hasDatePassed(nextIdentityDateFormat(user.children[0].lastIdentityDate))) {
                            ProgressUtil.createDialog(
                                requireContext(),
                                false,
                                "아이 신원 업데이트 필요",
                                "6개월 이상 아이 신원 업데이트가\n" +
                                        "되지 않았습니다 아이 정보를 정확히\n" +
                                        "반영하기 위해서 신원 업데이트가 필요합니다"
                            ) {
                                it.dismiss()
                                (activity as MainActivity).moveBottomNavigation(R.id.childrenInfo)
                            }
                        }
                    }
                }
            }

            viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

            viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                animationView.visibility = View.GONE
                childrenList.visibility = View.VISIBLE

                childrenList.layoutManager = LinearLayoutManager(requireContext().applicationContext)

                when (getSelectedItem(requireContext())) {
                    R.id.children -> {
                        childrenList.adapter = ChildrenInfoAdapter(apiData.filterChildren().subList(0, 3), ChildInfoType.NEW)
                    }
                    R.id.adult -> {
                        childrenList.adapter = ChildrenInfoAdapter(apiData.filterAdults().subList(0, 3), ChildInfoType.NEW)
                    }
                    R.id.all -> {
                        childrenList.adapter = ChildrenInfoAdapter(apiData.subList(0, 3), ChildInfoType.NEW)
                    }
                }
            }

            mainChildrenInfoRegistrationButton.setOnClickListener {
                (activity as MainActivity).moveBottomNavigation(R.id.childrenInfo)
            }

            searchButton.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_searchFragment)
            }

            childrenInfoButton.setOnClickListener {
                (activity as MainActivity).moveBottomNavigation(R.id.childrenInfo)
            }

            childrenListButton.setOnClickListener {
                (activity as MainActivity).moveBottomNavigation(R.id.childrenList)
            }

            reportChildrenButton.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.safe182.go.kr/cont/homeLogContents.do?contentsNm=report_info_182")
                    )
                )
            }

            settingButton.setOnClickListener {
                (activity as MainActivity).moveBottomNavigation(R.id.setting)
            }

            optionButton.setOnClickListener {
                showPopupMenu(requireContext(), it) {
                    viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                        childrenList.layoutManager = LinearLayoutManager(requireContext().applicationContext)

                        when (it) {
                            R.id.children -> {
                                childrenList.adapter = ChildrenInfoAdapter(apiData.filterChildren().subList(0, 3), ChildInfoType.NEW)
                            }
                            R.id.adult -> {
                                childrenList.adapter = ChildrenInfoAdapter(apiData.filterAdults().subList(0, 3), ChildInfoType.NEW)
                            }
                            R.id.all -> {
                                childrenList.adapter = ChildrenInfoAdapter(apiData.subList(0, 3), ChildInfoType.NEW)
                            }
                        }
                    }
                }
            }

            Glide.with(requireContext())
                .load(R.drawable.profile)
                .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                .into(profile)

            mainChildrenInfoRegistrationButton.setOnClickListener {
                (activity as MainActivity).moveBottomNavigation(R.id.childrenInfo)
            }

            childrenIdentificationButton.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("camera type", CameraType.Find)

                view.findNavController().navigate(R.id.action_home_to_cameraFragment, bundle)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd")
        val outputFormat = SimpleDateFormat("yyyy. MM. dd")

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }

    private fun nextIdentityDateFormat(inputDateStr: String): String {
        try {
            val inputDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val inputDate: Date = inputDateFormat.parse(inputDateStr) ?: return ""

            val calendar = Calendar.getInstance()
            calendar.time = inputDate
            calendar.add(Calendar.MONTH, 6)

            val outputDateFormat = SimpleDateFormat("yyyy. MM. dd", Locale.getDefault())

            return outputDateFormat.format(calendar.time)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "오류 발생 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            return ""
        }
    }

    private fun hasDatePassed(dateString: String): Boolean {
        val format = SimpleDateFormat("yyyy. MM. dd", Locale.getDefault())
        val providedDate: Date?

        try {
            providedDate = format.parse(dateString)
        } catch (e: Exception) {
            return false
        }

        val currentDate = Calendar.getInstance().time

        return !providedDate.after(currentDate)
    }
}