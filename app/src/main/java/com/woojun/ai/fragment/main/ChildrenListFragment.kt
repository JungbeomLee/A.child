package com.woojun.ai.fragment.main

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentChildrenListBinding
import com.woojun.ai.util.AiResult
import com.woojun.ai.util.ChildInfoType
import com.woojun.ai.util.ChildrenInfoAdapter
import com.woojun.ai.util.FilterOptionUtil
import com.woojun.ai.util.FilterOptionUtil.filterAdults
import com.woojun.ai.util.FilterOptionUtil.filterChildren
import com.woojun.ai.util.FilterOptionUtil.getSelectedItem
import com.woojun.ai.util.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ChildrenListFragment : Fragment() {

    private var _binding: FragmentChildrenListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ViewModel
    private var pageIndex = 0
    private var pageEndIndex = 0
    private lateinit var apiList: MutableList<List<AiResult>>
    private var selectButtonNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChildrenListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            viewModel = ViewModelProvider(requireActivity())[ViewModel::class.java]

            viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                animationView.visibility = View.GONE
                childrenList.visibility = View.VISIBLE

                apiList = mutableListOf()

                val filteredList = when (getSelectedItem(requireContext())) {
                    R.id.children -> {
                        apiData.filterChildren()
                    }
                    R.id.adult -> {
                        apiData.filterAdults()
                    }
                    R.id.all -> {
                        apiData
                    }
                    else -> {
                        apiData.filterChildren()
                    }
                }

                if (filteredList.size != 0) {
                    filteredList.chunked(5).forEach {
                        apiList.add(it)
                    }

                    pageEndIndex = apiList.size - 1
                    pageIndex = 0

                    pageNumberText.text = "${pageIndex+1}/${pageEndIndex+1} 페이지"

                    if (apiList.size != 0) {
                        childrenList.layoutManager = LinearLayoutManager(requireContext().applicationContext)
                        childrenList.adapter = ChildrenInfoAdapter(apiList[pageIndex].toMutableList(), ChildInfoType.DEFAULT)
                    }
                }
            }

            optionButton.setOnClickListener {
                FilterOptionUtil.showPopupMenu(requireContext(), it) {
                    selectButton(selectButtonNumber)
                }
            }

            buttonScrollView.post {
                val scrollWidth = buttonScrollView.getChildAt(0).width
                val viewWidth = buttonScrollView.width
                val middle = (scrollWidth - viewWidth) / 2
                buttonScrollView.smoothScrollTo(middle, 0)
            }

            newChildrenButton.setOnClickListener {

                selectButtonNumber = 0

                newChildrenText.setTextColor(Color.parseColor("#4894fe"))
                newChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBF5FF"))

                allChildrenText.setTextColor(Color.parseColor("#8696BB"))
                allChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                longChildrenText.setTextColor(Color.parseColor("#8696BB"))
                longChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                    animationView.visibility = View.GONE
                    childrenList.visibility = View.VISIBLE

                    apiList = mutableListOf()

                    val filteredList = when (getSelectedItem(requireContext())) {
                        R.id.children -> {
                            apiData.filterChildren()
                        }
                        R.id.adult -> {
                            apiData.filterAdults()
                        }
                        R.id.all -> {
                            apiData
                        }
                        else -> {
                            apiData.filterChildren()
                        }
                    }

                    if (filteredList.size != 0) {
                        filteredList.filter {
                            isWithinTwoDaysFromNow(it.occrde!!)
                        }.chunked(5).forEach {
                            apiList.add(it)
                        }

                        pageEndIndex = apiList.size - 1
                        pageIndex = 0

                        if (apiList.size != 0) {
                            pageNumberText.text = "${pageIndex+1}/${pageEndIndex+1} 페이지"
                            childrenList.adapter = ChildrenInfoAdapter(apiList[pageIndex].toMutableList(), ChildInfoType.DEFAULT)
                        }
                    }
                }

                buttonScrollView.post {
                    val animator = ValueAnimator.ofInt(buttonScrollView.scrollX, 0)
                    animator.duration = 500

                    animator.addUpdateListener { animation ->
                        val animatedValue = animation.animatedValue as Int
                        buttonScrollView.scrollTo(animatedValue, 0)
                    }

                    animator.start()
                }
            }

            allChildrenButton.setOnClickListener {

                selectButtonNumber = 1

                newChildrenText.setTextColor(Color.parseColor("#8696BB"))
                newChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                allChildrenText.setTextColor(Color.parseColor("#4894fe"))
                allChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBF5FF"))

                longChildrenText.setTextColor(Color.parseColor("#8696BB"))
                longChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                    animationView.visibility = View.GONE
                    childrenList.visibility = View.VISIBLE

                    apiList = mutableListOf()

                    val filteredList = when (getSelectedItem(requireContext())) {
                        R.id.children -> {
                            apiData.filterChildren()
                        }
                        R.id.adult -> {
                            apiData.filterAdults()
                        }
                        R.id.all -> {
                            apiData
                        }
                        else -> {
                            apiData.filterChildren()
                        }
                    }

                    if (filteredList.size != 0) {
                        filteredList.chunked(5).forEach {
                            apiList.add(it)
                        }

                        pageEndIndex = apiList.size - 1
                        pageIndex = 0

                        if (apiList.size != 0) {
                            pageNumberText.text = "${pageIndex+1}/${pageEndIndex+1} 페이지"
                            childrenList.adapter = ChildrenInfoAdapter(apiList[pageIndex].toMutableList(), ChildInfoType.DEFAULT)
                        }
                    }
                }

                val scrollWidth = buttonScrollView.getChildAt(0).width
                val viewWidth = buttonScrollView.width
                val middle = (scrollWidth - viewWidth) / 2

                val animator = ValueAnimator.ofInt(buttonScrollView.scrollX, middle)
                animator.duration = 500

                animator.addUpdateListener { animation ->
                    val animatedValue = animation.animatedValue as Int
                    buttonScrollView.scrollTo(animatedValue, 0)
                }

                animator.start()

            }

            longChildrenButton.setOnClickListener {

                selectButtonNumber = 2

                newChildrenText.setTextColor(Color.parseColor("#8696BB"))
                newChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                allChildrenText.setTextColor(Color.parseColor("#8696BB"))
                allChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                longChildrenText.setTextColor(Color.parseColor("#4894fe"))
                longChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBF5FF"))

                viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                    animationView.visibility = View.GONE
                    childrenList.visibility = View.VISIBLE

                    apiList = mutableListOf()

                    val filteredList = when (getSelectedItem(requireContext())) {
                        R.id.children -> {
                            apiData.filterChildren()
                        }
                        R.id.adult -> {
                            apiData.filterAdults()
                        }
                        R.id.all -> {
                            apiData
                        }
                        else -> {
                            apiData.filterChildren()
                        }
                    }

                    if (filteredList.size != 0) {
                        filteredList.filter {
                            isDate365DaysAgo(it.occrde!!)
                        }.chunked(5).forEach {
                            apiList.add(it)
                        }

                        pageEndIndex = apiList.size - 1
                        pageIndex = 0

                        if (apiList.size != 0) {
                            pageNumberText.text = "${pageIndex+1}/${pageEndIndex+1} 페이지"
                            childrenList.adapter = ChildrenInfoAdapter(apiList[pageIndex].toMutableList(), ChildInfoType.DEFAULT)
                        }
                    }
                }

                buttonScrollView.post {
                    val maxScrollAmount = buttonScrollView.getChildAt(0).width - buttonScrollView.width
                    val animator = ValueAnimator.ofInt(buttonScrollView.scrollX, maxScrollAmount)
                    animator.duration = 500

                    animator.addUpdateListener { animation ->
                        val animatedValue = animation.animatedValue as Int
                        buttonScrollView.scrollTo(animatedValue, 0)
                    }

                    animator.start()
                }
            }

            beforeButton.setOnClickListener {
                if (pageIndex - 1 == -1) {
                    Toast.makeText(requireContext(), "첫 페이지입니다", Toast.LENGTH_SHORT).show()
                } else {
                    pageIndex -= 1
                    childrenList.adapter = ChildrenInfoAdapter(apiList[pageIndex].toMutableList(), ChildInfoType.DEFAULT)
                    pageNumberText.text = "${pageIndex+1}/${pageEndIndex+1} 페이지"
                }

            }

            afterButton.setOnClickListener {
                if (pageIndex + 1 > pageEndIndex) {
                    Toast.makeText(requireContext(), "마지막 페이지입니다", Toast.LENGTH_SHORT).show()
                } else {
                    pageIndex += 1
                    childrenList.adapter = ChildrenInfoAdapter(apiList[pageIndex].toMutableList(), ChildInfoType.DEFAULT)
                    pageNumberText.text = "${pageIndex+1}/${pageEndIndex+1} 페이지"
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun selectButton(type: Int) {
        binding.apply {
            when (type) {
                0 -> {
                    newChildrenText.setTextColor(Color.parseColor("#4894fe"))
                    newChildrenButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#EBF5FF"))

                    allChildrenText.setTextColor(Color.parseColor("#8696BB"))
                    allChildrenButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                    longChildrenText.setTextColor(Color.parseColor("#8696BB"))
                    longChildrenButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                    viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                        animationView.visibility = View.GONE
                        childrenList.visibility = View.VISIBLE

                        apiList = mutableListOf()

                        val filteredList = when (getSelectedItem(requireContext())) {
                            R.id.children -> {
                                apiData.filterChildren()
                            }

                            R.id.adult -> {
                                apiData.filterAdults()
                            }

                            R.id.all -> {
                                apiData
                            }

                            else -> {
                                apiData.filterChildren()
                            }
                        }

                        if (filteredList.size != 0) {
                            filteredList.filter {
                                isWithinTwoDaysFromNow(it.occrde!!)
                            }.chunked(5).forEach {
                                apiList.add(it)
                            }

                            pageEndIndex = apiList.size - 1
                            pageIndex = 0

                            if (apiList.size != 0) {
                                pageNumberText.text = "${pageIndex + 1}/${pageEndIndex + 1} 페이지"
                                childrenList.adapter = ChildrenInfoAdapter(
                                    apiList[pageIndex].toMutableList(),
                                    ChildInfoType.DEFAULT
                                )
                            }
                        }
                    }

                    buttonScrollView.post {
                        val animator = ValueAnimator.ofInt(buttonScrollView.scrollX, 0)
                        animator.duration = 500

                        animator.addUpdateListener { animation ->
                            val animatedValue = animation.animatedValue as Int
                            buttonScrollView.scrollTo(animatedValue, 0)
                        }

                        animator.start()
                    }
                }
                1 -> {
                    newChildrenText.setTextColor(Color.parseColor("#8696BB"))
                    newChildrenButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                    allChildrenText.setTextColor(Color.parseColor("#4894fe"))
                    allChildrenButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#EBF5FF"))

                    longChildrenText.setTextColor(Color.parseColor("#8696BB"))
                    longChildrenButton.backgroundTintList =
                        ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                    viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                        animationView.visibility = View.GONE
                        childrenList.visibility = View.VISIBLE

                        apiList = mutableListOf()

                        val filteredList = when (getSelectedItem(requireContext())) {
                            R.id.children -> {
                                apiData.filterChildren()
                            }

                            R.id.adult -> {
                                apiData.filterAdults()
                            }

                            R.id.all -> {
                                apiData
                            }

                            else -> {
                                apiData.filterChildren()
                            }
                        }

                        if (filteredList.size != 0) {
                            filteredList.chunked(5).forEach {
                                apiList.add(it)
                            }

                            pageEndIndex = apiList.size - 1
                            pageIndex = 0

                            if (apiList.size != 0) {
                                pageNumberText.text = "${pageIndex + 1}/${pageEndIndex + 1} 페이지"
                                childrenList.adapter = ChildrenInfoAdapter(
                                    apiList[pageIndex].toMutableList(),
                                    ChildInfoType.DEFAULT
                                )
                            }
                        }
                    }
                }
                2 -> {
                    newChildrenText.setTextColor(Color.parseColor("#8696BB"))
                    newChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                    allChildrenText.setTextColor(Color.parseColor("#8696BB"))
                    allChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FAFAFA"))

                    longChildrenText.setTextColor(Color.parseColor("#4894fe"))
                    longChildrenButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EBF5FF"))

                    viewModel.getApiData().observe(viewLifecycleOwner) { apiData ->

                        animationView.visibility = View.GONE
                        childrenList.visibility = View.VISIBLE

                        apiList = mutableListOf()

                        val filteredList = when (getSelectedItem(requireContext())) {
                            R.id.children -> {
                                apiData.filterChildren()
                            }
                            R.id.adult -> {
                                apiData.filterAdults()
                            }
                            R.id.all -> {
                                apiData
                            }
                            else -> {
                                apiData.filterChildren()
                            }
                        }

                        if (filteredList.size != 0) {
                            filteredList.filter {
                                isDate365DaysAgo(it.occrde!!)
                            }.chunked(5).forEach {
                                apiList.add(it)
                            }

                            pageEndIndex = apiList.size - 1
                            pageIndex = 0

                            if (apiList.size != 0) {
                                pageNumberText.text = "${pageIndex+1}/${pageEndIndex+1} 페이지"
                                childrenList.adapter = ChildrenInfoAdapter(apiList[pageIndex].toMutableList(), ChildInfoType.DEFAULT)
                            }
                        }
                    }

                    buttonScrollView.post {
                        val maxScrollAmount = buttonScrollView.getChildAt(0).width - buttonScrollView.width
                        val animator = ValueAnimator.ofInt(buttonScrollView.scrollX, maxScrollAmount)
                        animator.duration = 500

                        animator.addUpdateListener { animation ->
                            val animatedValue = animation.animatedValue as Int
                            buttonScrollView.scrollTo(animatedValue, 0)
                        }

                        animator.start()
                    }
                }
            }
        }

    }

    private fun isWithinTwoDaysFromNow(targetDateString: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        try {
            val currentDate = Calendar.getInstance().time

            val targetDate = dateFormat.parse(targetDateString)

            val twoDaysAgo = Calendar.getInstance()
            twoDaysAgo.add(Calendar.DATE, -2)

            if (targetDate != null) {
                return targetDate >= twoDaysAgo.time && targetDate <= currentDate
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "오류 발생 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
        }

        return false
    }


    private fun isDate365DaysAgo(inputDateStr: String): Boolean {
        return try {
            val currentDate = Date()

            val dateFormat = SimpleDateFormat("yyyyMMdd")
            val inputDate = dateFormat.parse(inputDateStr)

            val timeDifference = currentDate.time - inputDate.time

            timeDifference >= (365L * 24 * 60 * 60 * 1000)
        } catch (e: Exception) {
            false
        }
    }

}