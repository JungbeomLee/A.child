package com.woojun.ai.fragment.main

import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentRePersonalInformationBinding
import com.woojun.ai.util.AppDatabase
import com.woojun.ai.util.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RePersonalInformationFragment : Fragment() {

    private var _binding: FragmentRePersonalInformationBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRePersonalInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        database = Firebase.database.reference
        binding.apply {

            val builder = dialog()
            builder.show()

            finishButton.setOnClickListener {
                when (titleText.text) {
                    "변경하실 이름을 입력하세요" -> {
                        database.child("users").child(auth.uid.toString()).child("name").setValue(inputArea.text.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getDatabase(requireContext())
                            val userDao = db!!.userInfoDao()
                            val user = userDao.getUser()

                            userDao.updateUser(UserInfo(inputArea.text.toString(), user.email, user.phoneNumber, user.policeCheck, user.children, user.registrationCheck))
                        }
                        Toast.makeText(requireContext(), "이름 변경이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    }

                    "변경하실 이메일을 입력하세요" -> {
                        database.child("users").child(auth.uid.toString()).child("email").setValue(inputArea.text.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getDatabase(requireContext())
                            val userDao = db!!.userInfoDao()
                            val user = userDao.getUser()

                            userDao.updateUser(UserInfo(user.name, inputArea.text.toString(), user.phoneNumber, user.policeCheck, user.children, user.registrationCheck))
                        }
                        Toast.makeText(requireContext(), "이메일 변경이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    }

                    "변경하실 전화번호를 입력하세요" -> {
                        database.child("users").child(auth.uid.toString()).child("phoneNumber").setValue(inputArea.text.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getDatabase(requireContext())
                            val userDao = db!!.userInfoDao()
                            val user = userDao.getUser()

                            userDao.updateUser(UserInfo(user.name, user.email, inputArea.text.toString(), user.policeCheck, user.children, user.registrationCheck))
                        }
                        Toast.makeText(requireContext(), "전화번호 변경이 완료되었습니다", Toast.LENGTH_SHORT).show()
                    }

                    "이메일을 입력하세요" -> {
                        auth.sendPasswordResetEmail(inputArea.text.toString()).addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(requireContext(),"이메일을 보냈습니다.",Toast.LENGTH_LONG).show()
                            }else{
                                Toast.makeText(requireContext(),"이메일 발송이 실패했습니다.",Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun dialog(): AlertDialog.Builder {
        val optionList = arrayOf("이름 재설정", "이메일 재설정", "전화번호 재설정", "비밀번호 재설정")
        val selectItem = 0
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialogStyle)

        builder.setSingleChoiceItems(optionList, selectItem
            ) { _, p1 ->
                binding.apply {
                    when (p1) {
                        0 -> {
                            titleText.text = "변경하실 이름을 입력하세요"
                            subText.text = "입력하신 이름으로 계정의 이름을\n재설정 해드립니다 ex)홍길동"
                            inputLayout.hint = "이름"
                            inputArea.inputType = InputType.TYPE_CLASS_TEXT
                            finishButtonText.text = "이름 제출"
                        }

                        1 -> {
                            titleText.text = "변경하실 이메일을 입력하세요"
                            subText.text = "입력하신 이메일로 계정의 이메일을\n재설정 해드립니다 ex)abc@naver.com"
                            inputLayout.hint = "이메일"
                            inputArea.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            finishButtonText.text = "이메일 제출"
                        }

                        2 -> {
                            titleText.text = "변경하실 전화번호를 입력하세요"
                            subText.text = "입력하신 전화번호로 계정의 전화번호를\n재설정 해드립니다 ex)01012345678"
                            inputLayout.hint = "전화번호"
                            inputArea.inputType = InputType.TYPE_CLASS_PHONE
                            finishButtonText.text = "전화번호 제출"
                        }

                        3 -> {
                            titleText.text = "이메일을 입력하세요"
                            subText.text = "입력하신 이메일 주소로 비밀번호\n재설정 메일을 보내드립니다 ex)abc@naver.com"
                            inputLayout.hint = "이메일"
                            inputArea.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                            finishButtonText.text = "이메일 제출"
                        }
                    }
                }
            }
        builder.create()

        return builder
    }

}