package com.woojun.ai.fragment.main

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.woojun.ai.MainActivity
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentMyChildInfoRegisterBinding
import com.woojun.ai.util.AppDatabase
import com.woojun.ai.util.CameraType
import com.woojun.ai.util.ChildInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyChildInfoRegisterFragment : Fragment() {

    private var _binding: FragmentMyChildInfoRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    private var sex = "남성"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyChildInfoRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            auth = Firebase.auth

            val mainActivity = activity as MainActivity
            mainActivity.hideBottomNavigation(true)

            val bundle = arguments
            val childInfoItem = bundle?.getParcelable<ChildInfo>("child info")

            if (childInfoItem != null) {

                nameArea.setText(childInfoItem.name)

                when (childInfoItem.sex) {
                    "남성" -> {
                        manBackground.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4894FE"))
                        manIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                        manText.setTextColor(Color.parseColor("#4894FE"))

                        womanBackground.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                        womanIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#000000"))
                        womanText.setTextColor(Color.parseColor("#000000"))

                        sex = "남성"
                    }
                    "여성" -> {
                        manBackground.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                        manIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#000000"))
                        manText.setTextColor(Color.parseColor("#000000"))

                        womanBackground.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4894FE"))
                        womanIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                        womanText.setTextColor(Color.parseColor("#4894FE"))

                        sex = "여성"
                    }
                }

                yearArea.setText(childInfoItem.birthDate.substring(0, 4))
                monthArea.setText(childInfoItem.birthDate.substring(4, 6))
                dateArea.setText(childInfoItem.birthDate.substring(6, 8))

                characteristicsArea.setText(childInfoItem.characteristics)

            }

            selectManButton.setOnClickListener {
                manBackground.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4894FE"))
                manIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                manText.setTextColor(Color.parseColor("#4894FE"))

                womanBackground.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                womanIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#000000"))
                womanText.setTextColor(Color.parseColor("#000000"))

                sex = "남성"
            }

            selectWomanButton.setOnClickListener {
                manBackground.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                manIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#000000"))
                manText.setTextColor(Color.parseColor("#000000"))

                womanBackground.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4894FE"))
                womanIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                womanText.setTextColor(Color.parseColor("#4894FE"))

                sex = "여성"
            }

            yearAreaField.setOnClickListener {
                yearArea.requestFocus()
            }

            monthAreaField.setOnClickListener {
                monthArea.requestFocus()
            }

            dateAreaField.setOnClickListener {
                dateArea.requestFocus()
            }

            characteristicsAreaField.setOnClickListener {
                characteristicsArea.requestFocus()
            }

            cameraButton.setOnClickListener {
                if (childInfoItem == null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getDatabase(requireContext())
                        val user = db!!.userInfoDao().getUser()

                        val childInfo = ChildInfo(
                            id = auth.uid+user.children.size,
                            name = nameArea.text.toString().replace(" ", ""),
                            birthDate = "${yearArea.text}${monthArea.text}${dateArea.text}",
                            sex = sex,
                            characteristics = characteristicsArea.text.toString(),
                            photo = "null",
                            lastIdentityDate = getToday(),
                            parentPhoneNumber = user.phoneNumber
                        )

                        withContext(Dispatchers.Main) {
                            if (validationCheck(childInfo)) {
                                val item = Bundle()
                                item.putParcelable("camera type", CameraType.ChildRegister)
                                item.putParcelable("child info", childInfo)

                                view.findNavController().navigate(R.id.action_myChildInfoRegisterFragment_to_cameraFragment, item)
                            }
                        }
                    }
                } else {
                    childInfoItem.name = nameArea.text.toString().replace(" ", "")
                    childInfoItem.birthDate = "${yearArea.text}${monthArea.text}${dateArea.text}"
                    childInfoItem.sex = sex
                    childInfoItem.characteristics = characteristicsArea.text.toString()
                    childInfoItem.lastIdentityDate = getToday()

                    if (validationCheck(childInfoItem)) {
                        val item = Bundle()
                        item.putParcelable("camera type", CameraType.ChildRegister)
                        item.putParcelable("child info", childInfoItem)

                        view.findNavController().navigate(R.id.action_myChildInfoRegisterFragment_to_cameraFragment, item)
                    }

                }

            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getToday(): String {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun validationCheck(childInfo: ChildInfo): Boolean {
        binding.apply {

            if (childInfo.name.isEmpty()) {
                Toast.makeText(requireContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                return false
            } else if (containsEnglishOrSpecialChars(childInfo.name)) {
                Toast.makeText(requireContext(), "특수문자 및 영어는 제외해주세요", Toast.LENGTH_SHORT).show()
                return false
            }

            if (!isValidDate(childInfo.birthDate)) {
                Toast.makeText(requireContext(), "생년월일을 올바르게 입력해주세요", Toast.LENGTH_SHORT).show()
                return false
            }

            return true
        }
    }

    private fun isValidDate(dateStr: String): Boolean {
        if (dateStr.length != 8) {
            return false
        }

        val year = dateStr.substring(0, 4).toInt()
        val month = dateStr.substring(4, 6)
        val date = dateStr.substring(6, 8)

        if (year !in 1900 .. getToday().substring(0, 4).toInt()) {
            return false
        }

        if (month.substring(0, 1).toInt() != 0 && month.substring(0, 1).toInt() != 1) {
            return false
        }

        if (month.substring(0, 1).toInt() == 1 && month.substring(1, 2).toInt() > 2) {
            return false
        }

        if (date.toInt() > 31) {
            return false
        }

        val daysInMonth = when (month.toInt()) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            else -> if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) 29 else 28
        }

        if (date.toInt() > daysInMonth) {
            return false
        }

        return true
    }

    private fun containsEnglishOrSpecialChars(input: String): Boolean {
        return input.matches(".*[a-zA-Z\\p{Punct}].*".toRegex())
    }
}