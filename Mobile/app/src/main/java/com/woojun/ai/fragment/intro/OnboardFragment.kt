package com.woojun.ai.fragment.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.woojun.ai.R
import com.woojun.ai.databinding.FragmentOnboardBinding
import com.woojun.ai.util.IntroPagerRecyclerAdapter
import com.woojun.ai.util.PagerItem

class OnboardFragment : Fragment() {

    private var _binding: FragmentOnboardBinding? = null
    private val binding get() = _binding!!

    private var pagerItemList = arrayListOf(PagerItem(R.drawable.onboard1, "집에서 간편하게!" , "장소에 구애받지 않고 간편하게\n우리 아이신원 등록할 수 있어요"), PagerItem(R.drawable.onboard2, "얼굴 사진 한장만으로!", "아이의 신원을 정확하고\n빠르게 파악할 수 있어요"), PagerItem(R.drawable.onboard3, "실종아동 전문 서비스", "혹시 모를 상황에 대비할 수 있는\n아동 신원 등록/확인 서비스"))
    private lateinit var introPagerRecyclerAdapter: IntroPagerRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            val registrationCheck = arguments?.getBoolean("registrationCheck")!!

            introPagerRecyclerAdapter = IntroPagerRecyclerAdapter(pagerItemList)

            binding.introViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 2) {
                        binding.buttonView.visibility = View.VISIBLE
                    } else {
                        binding.buttonView.visibility = View.GONE
                    }
                }
            })

            binding.introViewPager.apply {
                adapter = introPagerRecyclerAdapter
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                binding.dotsIndicator.attachTo(this)
            }

            binding.signUpButton.setOnClickListener {
                val bundle = Bundle()
                bundle.putBoolean("registrationCheck", registrationCheck)
                view.findNavController().navigate(R.id.action_onboardFragment_to_signUpFragment, bundle)
            }

            binding.loginButton.setOnClickListener {
                view.findNavController().navigate(R.id.action_onboardFragment_to_loginFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}