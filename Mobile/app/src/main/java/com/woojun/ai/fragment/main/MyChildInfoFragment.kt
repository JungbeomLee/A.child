package com.woojun.ai.fragment.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentMyChildInfoBinding
import com.woojun.ai.util.AppDatabase
import com.woojun.ai.util.MyChildAdapterType
import com.woojun.ai.util.MyChildInfoAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyChildInfoFragment : Fragment() {

    private var _binding: FragmentMyChildInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyChildInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            childrenInfoRegistrationButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(requireContext())
                    val userDao = db!!.userInfoDao()
                    var user = userDao.getUser()

                    withContext(Dispatchers.Main) {
                        if (user.registrationCheck) {
                            view.findNavController().navigate(R.id.action_childrenInfo_to_myChildInfoRegisterFragment)
                        } else {
                            Toast.makeText(requireContext(), "아이 등록을 사용하시려면 선택약관 동의가 필요합니다", Toast.LENGTH_SHORT).show()
                            showReAgreementDialog {
                                view.findNavController().navigate(R.id.action_childrenInfo_to_myChildInfoRegisterFragment)
                            }
                        }
                    }
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(requireContext())
                val user = db!!.userInfoDao().getUser()

                withContext(Dispatchers.Main) {
                    myChildrenList.layoutManager = LinearLayoutManager(requireContext().applicationContext)
                    myChildrenList.adapter = MyChildInfoAdapter(user.children, MyChildAdapterType.DEFAULT)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showReAgreementDialog(function: () -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.re_agreement_dialog, null)

        val allCheckBox = dialogView.findViewById<CheckBox>(R.id.all_check_box)
        val consentCheckBox1 = dialogView.findViewById<CheckBox>(R.id.consent_check_box1)
        val consentCheckBox2 = dialogView.findViewById<CheckBox>(R.id.consent_check_box2)

        val consentButton1 = dialogView.findViewById<ConstraintLayout>(R.id.consent_button_1)
        val consentButton2 = dialogView.findViewById<ConstraintLayout>(R.id.consent_button_2)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()

        val layoutParams = WindowManager.LayoutParams()
        val window = dialog.window
        layoutParams.copyFrom(window?.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = layoutParams

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        allCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                allCheckBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4894fe"))
                Toast.makeText(requireContext(), "약관동의 완료", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    CoroutineScope(Dispatchers.IO).launch {
                        val db = AppDatabase.getDatabase(requireContext())
                        val userDao = db!!.userInfoDao()
                        var user = userDao.getUser()

                        user.registrationCheck = true

                        userDao.updateUser(user)

                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                            function()
                        }
                    }
                }, 500)
            } else {
                allCheckBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#c8c8c8"))
            }
        }
        consentCheckBox1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                consentCheckBox1.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4894fe"))
            } else {
                consentCheckBox1.buttonTintList = ColorStateList.valueOf(Color.parseColor("#c8c8c8"))
            }
        }
        consentCheckBox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                consentCheckBox2.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4894fe"))
            } else {
                consentCheckBox2.buttonTintList = ColorStateList.valueOf(Color.parseColor("#c8c8c8"))
            }
        }

        consentButton1.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.webview_dialog)
            val windowWidth = resources.displayMetrics.widthPixels * 0.9
            val windowHeight = resources.displayMetrics.heightPixels * 0.7
            dialog.window?.setLayout(windowWidth.toInt(), windowHeight.toInt())

            val ok = dialog.findViewById<CheckBox>(R.id.yes_button)
            val x = dialog.findViewById<CheckBox>(R.id.no_button)
            val webView = dialog.findViewById<WebView>(R.id.webview)

            webView.loadUrl("https://sch10719.neocities.org/%E1%84%83%E1%85%A9%E1%86%BC%E1%84%8B%E1%85%B4%E1%84%89%E1%85%A52")

            ok.setOnClickListener {
                dialog.dismiss()
                consentCheckBox1.isChecked = true
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
            }

            x.setOnClickListener {
                dialog.dismiss()
                consentCheckBox1.isChecked = false
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
            }

            dialog.show()
        }
        consentButton2.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.webview_dialog)
            val windowWidth = resources.displayMetrics.widthPixels * 0.9
            val windowHeight = resources.displayMetrics.heightPixels * 0.7
            dialog.window?.setLayout(windowWidth.toInt(), windowHeight.toInt())

            val ok = dialog.findViewById<CheckBox>(R.id.yes_button)
            val x = dialog.findViewById<CheckBox>(R.id.no_button)
            val webView = dialog.findViewById<WebView>(R.id.webview)

            webView.loadUrl("https://sch10719.neocities.org/%E1%84%83%E1%85%A9%E1%86%BC%E1%84%8B%E1%85%B4%E1%84%89%E1%85%A53")

            ok.setOnClickListener {
                dialog.dismiss()
                consentCheckBox1.isChecked = true
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
            }

            x.setOnClickListener {
                dialog.dismiss()
                consentCheckBox1.isChecked = false
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
            }

            dialog.show()
        }

        allCheckBox.setOnClickListener {
            if (allCheckBox.isChecked) {
                consentCheckBox1.isChecked = true
                consentCheckBox2.isChecked = true
            } else {
                consentCheckBox1.isChecked = false
                consentCheckBox2.isChecked = false
            }
        }
        consentCheckBox1.setOnClickListener {
            allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
        }
        consentCheckBox2.setOnClickListener {
            allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
        }

    }


}