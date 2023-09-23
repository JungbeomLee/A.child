package com.woojun.ai.fragment.intro

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.woojun.ai.MainActivity
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentLoginBinding
import com.woojun.ai.util.AppDatabase
import com.woojun.ai.util.ProgressUtil.createLoadingDialog
import com.woojun.ai.util.SimilarDistanceUid
import com.woojun.ai.util.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        binding.apply {

            textInputLayoutPaddingSetting()

            passwordArea.setOnEditorActionListener(getEditorActionListener(loginButton))
            passwordInputLayout.editText?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    passwordInputLayout.hint = "영문, 특수문자 포함 숫자 8자리"
                } else {
                    passwordInputLayout.hint = "비밀번호"
                }
            }

            forgotPasswordButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_loginFragment_to_resetPasswordFragment)
            }

            moveSignUpText.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            moveSignUpButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

            loginButton.setOnClickListener {
                emailInputLayout.isErrorEnabled = false
                passwordInputLayout.isErrorEnabled = false

                val loginCheck = validationCheck(
                    emailArea.text.toString().trim(),
                    passwordArea.text.toString().trim()
                )

                if (loginCheck) {
                    loginUser(emailArea.text.toString().trim(), passwordArea.text.toString().trim())
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getEditorActionListener(view: View): TextView.OnEditorActionListener {
        return TextView.OnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                view.callOnClick()
            }
            false
        }
    }

    private fun validationCheck(email: String, password: String): Boolean {
        binding.apply {

            if (email.isEmpty()) {
                emailInputLayout.error = "이메일을 입력해주세요"
                return false
            } else if (!isEmailValid(email)) {
                emailInputLayout.error = "유효한 이메일 형식이 아닙니다"
                return false
            }

            if (password.isEmpty()) {
                passwordInputLayout.error = "비밀번호를 입력해주세요"
                return false
            } else if (!isPasswordValid(password)) {
                passwordInputLayout.error = "영문, 숫자, 특수문자 조합으로 8자 이상이 필요합니다"
                return false
            }

            emailInputLayout.error = ""
            passwordInputLayout.error = ""

            return true
        }
    }

    private fun textInputLayoutPaddingSetting() {
        binding.apply {

            emailInputLayout.apply {
                viewTreeObserver.addOnGlobalLayoutListener {
                    if (childCount > 1) {
                        getChildAt(1)?.setPadding(
                            8,
                            20,
                            0,
                            0
                        )
                    }
                }
            }

            passwordInputLayout.apply {
                viewTreeObserver.addOnGlobalLayoutListener {
                    if (childCount > 1) {
                        getChildAt(1)?.setPadding(
                            8,
                            20,
                            0,
                            0
                        )
                    }
                }
            }

        }
    }


    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")
        return emailRegex.matches(email)
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+\$).{8,}\$")
        return passwordRegex.matches(password)
    }

    private fun loginUser(email: String, password: String) {
        binding.loginButton.isEnabled = false
        database = Firebase.database.reference

        val auth = FirebaseAuth.getInstance()
        val (loadingDialog, setDialogText) = createLoadingDialog(requireContext())
        loadingDialog.show()
        setDialogText("로그인 시도중")

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    setDialogText("데이터베이스 조회중")

                    database.child("users").child(auth.uid.toString()).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val value = snapshot.getValue(UserInfo::class.java)

                                CoroutineScope(Dispatchers.IO).launch {
                                    val db = AppDatabase.getDatabase(requireContext())
                                    val userDao = db!!.userInfoDao()
                                    val findChildDao = db.findChildDao()

                                    userDao.insertUser(value!!)
                                    findChildDao.insertFindChild(SimilarDistanceUid(0, similarDistanceUid = listOf()))

                                    withContext(Dispatchers.Main) {
                                        setDialogText("로그인 성공")
                                        Handler().postDelayed({
                                            loadingDialog.dismiss()
                                            startActivity(Intent(requireContext(), MainActivity::class.java))
                                            finishAffinity(requireActivity())
                                        }, 500)
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            setDialogText("로그인 실패")
                            Handler().postDelayed({
                                loadingDialog.dismiss()
                            }, 500)
                        }
                    })
                } else {
                    setDialogText("로그인 실패")
                    Handler().postDelayed({
                        loadingDialog.dismiss()
                        try {
                            throw task.exception!!
                        } catch (e: Exception) {
                            binding.emailInputLayout.error = "해당 이메일 또는 비밀번호가 유효하지 않습니다"
                        }
                    }, 500)
                }
                binding.loginButton.isEnabled = true
            }
    }

}