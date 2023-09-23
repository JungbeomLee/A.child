package com.woojun.ai.fragment.intro

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentAgreementBinding

class AgreementFragment : Fragment() {

    private var _binding: FragmentAgreementBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgreementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            allCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    allCheckBox.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4894fe"))
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

            consentCheckBox3.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    consentCheckBox3.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4894fe"))
                } else {
                    consentCheckBox3.buttonTintList = ColorStateList.valueOf(Color.parseColor("#c8c8c8"))
                }
            }

            consentCheckBox4.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    consentCheckBox4.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4894fe"))
                } else {
                    consentCheckBox4.buttonTintList = ColorStateList.valueOf(Color.parseColor("#c8c8c8"))
                }
            }

            consentCheckBox5.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    consentCheckBox5.buttonTintList = ColorStateList.valueOf(Color.parseColor("#4894fe"))
                } else {
                    consentCheckBox5.buttonTintList = ColorStateList.valueOf(Color.parseColor("#c8c8c8"))
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

                webView.loadUrl("https://sch10719.neocities.org/%E1%84%83%E1%85%A9%E1%86%BC%E1%84%8B%E1%85%B4%E1%84%89%E1%85%A51")

                ok.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox1.isChecked = true
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                x.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox1.isChecked = false
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
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

                val okText = dialog.findViewById<TextView>(R.id.yes_text)
                val xText = dialog.findViewById<TextView>(R.id.no_text)
                val contextText = dialog.findViewById<TextView>(R.id.context_text)

                val webView = dialog.findViewById<WebView>(R.id.webview)

                webView.visibility = View.GONE
                contextText.text = "만 14세 미만 아동은 서비스 회원가입을 할 수 없습니다."
                okText.text = "만 14세 이상입니다"
                xText.text = "만 14세 미만입니다"

                okText.textSize = 12f
                xText.textSize = 12f

                ok.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox2.isChecked = true
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                x.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox2.isChecked = false
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                dialog.show()
            }

            consentButton3.setOnClickListener {
                val dialog = Dialog(requireContext())
                dialog.setContentView(R.layout.webview_dialog)
                val windowWidth = resources.displayMetrics.widthPixels * 0.9
                val windowHeight = resources.displayMetrics.heightPixels * 0.7
                dialog.window?.setLayout(windowWidth.toInt(), windowHeight.toInt())

                val ok = dialog.findViewById<CheckBox>(R.id.yes_button)
                val x = dialog.findViewById<CheckBox>(R.id.no_button)
                val webView = dialog.findViewById<WebView>(R.id.webview)

                webView.loadUrl("https://sch10719.neocities.org/%E1%84%87%E1%85%A1%E1%86%BC%E1%84%8E%E1%85%B5%E1%86%B7")

                ok.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox3.isChecked = true
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                x.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox3.isChecked = false
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                dialog.show()
            }

            consentButton4.setOnClickListener {
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
                    consentCheckBox4.isChecked = true
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                x.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox4.isChecked = false
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                dialog.show()
            }

            consentButton5.setOnClickListener {
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
                    consentCheckBox5.isChecked = true
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                x.setOnClickListener {
                    dialog.dismiss()
                    consentCheckBox5.isChecked = false
                    allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                            && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
                }

                dialog.show()
            }

            allCheckBox.setOnClickListener {
                if (allCheckBox.isChecked) {
                    consentCheckBox1.isChecked = true
                    consentCheckBox2.isChecked = true
                    consentCheckBox3.isChecked = true
                    consentCheckBox4.isChecked = true
                    consentCheckBox5.isChecked = true
                } else {
                    consentCheckBox1.isChecked = false
                    consentCheckBox2.isChecked = false
                    consentCheckBox3.isChecked = false
                    consentCheckBox4.isChecked = false
                    consentCheckBox5.isChecked = false
                }
            }

            consentCheckBox1.setOnClickListener {
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                        && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
            }

            consentCheckBox2.setOnClickListener {
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                        && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
            }

            consentCheckBox3.setOnClickListener {
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                        && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
            }

            consentCheckBox4.setOnClickListener {
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                        && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
            }

            consentCheckBox5.setOnClickListener {
                allCheckBox.isChecked = consentCheckBox1.isChecked && consentCheckBox2.isChecked
                        && consentCheckBox3.isChecked && consentCheckBox4.isChecked && consentCheckBox5.isChecked
            }

            startButton.setOnClickListener {

                val checked = consentCheckBox1.isChecked && consentCheckBox2.isChecked && consentCheckBox3.isChecked

                if (checked) {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.agreementFragment, true)
                        .build()

                    val bundle = Bundle()
                    if (consentCheckBox4.isChecked && consentCheckBox5.isChecked) {
                        bundle.putBoolean("registrationCheck", true)
                    } else {
                        bundle.putBoolean("registrationCheck", false)
                    }

                    view.findNavController().navigate(
                        R.id.action_agreementFragment_to_onboardFragment,
                        bundle,
                        navOptions,
                    )
                } else {
                    Toast.makeText(requireContext().applicationContext, "필수 약관들을 모두 동의해주세요", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}