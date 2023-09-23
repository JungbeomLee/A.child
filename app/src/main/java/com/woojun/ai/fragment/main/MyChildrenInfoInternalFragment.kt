package com.woojun.ai.fragment.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentMyChildrenInfoInternalBinding
import com.woojun.ai.util.ChildInfo
import com.woojun.ai.util.MyChildAdapterType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MyChildrenInfoInternalFragment : Fragment() {

    private var _binding: FragmentMyChildrenInfoInternalBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyChildrenInfoInternalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            val bundle = arguments
            val childInfo = bundle!!.getParcelable<ChildInfo>("child info")
            val childInfoType = bundle.getParcelable<MyChildAdapterType>("child info type")

            name.text = childInfo!!.name
            age.text = "${calculateAge(childInfo.birthDate)}"
            sex.text = childInfo.sex
            birthday.text = formatDate(childInfo.birthDate)
            lastIdentityDate.text = formatDate(childInfo.lastIdentityDate)
            characteristics.text = childInfo.characteristics
            Glide.with(binding.root.context)
                .load(childInfo.photo)
                .error(R.drawable.child)
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                .format(DecodeFormat.PREFER_RGB_565)
                .thumbnail(0.5f)
                .into(profile)

            chatButton.setOnClickListener {
                if (childInfoType == MyChildAdapterType.DEFAULT) {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:182"))
                    intent.putExtra(
                        "sms_body",
                        "제 자녀 ${childInfo.name}이 (입력)쪽에서 실종 되었습니다 " +
                                "실종 시각은 (입력)이고 실종 당시 복장은 (입력), 특이사항으로는 (입력)이 있습니다"
                    )
                    startActivity(intent)
                } else {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${childInfo.parentPhoneNumber}"))
                    intent.putExtra(
                        "sms_body",
                        "${childInfo.name}로 의심되는 학생이 (입력)쪽에서 발견 되었습니다 " +
                                "목격 시각은 (입력)이고 복장은 (입력)이었으며 특이사항으로는 (입력)이 있습니다"
                    )
                    startActivity(intent)
                }
            }

            phoneButton.setOnClickListener {
                if (childInfoType == MyChildAdapterType.DEFAULT) {
                    startActivity(
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                    )
                } else {
                    startActivity(
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:${childInfo.parentPhoneNumber}"))
                    )
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMdd")
        val outputFormat = SimpleDateFormat("yy.MM.dd")

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }

    private fun calculateAge(birthdateStr: String): Int {
        try {
            val birthdateDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val birthdate: Date = birthdateDateFormat.parse(birthdateStr) ?: return -1

            val currentDate = Calendar.getInstance().time

            val birthdateCalendar = Calendar.getInstance()
            birthdateCalendar.time = birthdate
            val currentCalendar = Calendar.getInstance()
            currentCalendar.time = currentDate

            return currentCalendar.get(Calendar.YEAR) - birthdateCalendar.get(Calendar.YEAR)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "오류 발생 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            return -1
        }
    }
}