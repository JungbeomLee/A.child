package com.woojun.ai

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.woojun.ai.databinding.ActivityMainBinding
import com.woojun.ai.util.ProgressUtil
import com.woojun.ai.util.ViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0 // 뒤로가기 시간 변수
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!isNetworkAvailable(this)){ // 만약 인터넷 연결에 실패했다면
            ProgressUtil.createDialog( // 실패 Dialog 보여주기
                this,
                false,
                "인터넷 연결 실패",
                "인터넷 연결에 실패하였습니다\n" +
                        "A·아이를 이용하시려면 인터넷을 필요합니다\n" +
                        "다시 한번 인터넷 연결이 되었는지 확인해주세요"
            ) {// 버튼 클릭 시
                if (isNetworkAvailable(this)) { // 다시 인터넷이 연결되었다면
                    it.dismiss() // Dialog 삭제
                } else { // 인터넷 연결이 안됐다면
                    Toast.makeText(this, "인터넷을 확인해주세요", Toast.LENGTH_SHORT).show() // 안내 메시지
                }
            }
        }

        // ViewModel 세팅
        viewModel = ViewModelProvider(this)[ViewModel::class.java]
        viewModel.loadApiData()

        // statusBar 색 설정
        window.statusBarColor = Color.TRANSPARENT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // navigation 세팅
        val navController = findNavController(R.id.nav_host_fragment)

        binding.apply {
            bottomNavigation.setItemSelected(R.id.home) // 첫 번째 화면 설정
            bottomNavigation.setOnItemSelectedListener { // bottom navigation 아이템 클릭 시
                moveBottomNavigation(it) // moveBottomNavigation 함수 실행
            }

            navController.addOnDestinationChangedListener { _, destination, _ -> // 만약 bottom navigation을 이동했다면
                bottomNavigation.setItemSelected(destination.id) // 선택된 아이템 변경
            }
        }


    }

    override fun onBackPressed() { // 뒤로가기 클릭 시

        // bottom navigation 설정
        val navController = findNavController(R.id.nav_host_fragment)

        // destination 설정
        val currentDestinationId = navController.currentDestination?.id
        val previousDestinationId = navController.previousBackStackEntry?.destination?.id

        if (currentDestinationId != R.id.home) { // 만약 현재 destination이 home이 아니라면
            when (previousDestinationId) {
                R.id.childrenList -> navController.popBackStack(R.id.childrenList, false)
                R.id.findChildrenListFragment -> navController.popBackStack(R.id.findChildrenListFragment, false)
                else -> navController.popBackStack(R.id.home, false)
            }
        } else { // 만약 home 화면이라면
            if (System.currentTimeMillis() - backPressedTime >= 2000) {  // 만약 뒤로가기 누른지 2초가 넘는다면
                backPressedTime = System.currentTimeMillis() // backPressedTime 세팅
                Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show() // 안내 메시지 봉여주기
            } else { // 2초전에 한번 더 눌렀다면
                finish() // 종료
            }
        }
        hideBottomNavigation(false) // hideBottomNavigation 호출 (보여주기)
    }

    fun moveBottomNavigation(it: Int) { // bottom navigation 이동 함수
        binding.apply {
            // bottom navigation 설정
            val navController = findNavController(R.id.nav_host_fragment)

            // 받은 인자에 맞게 Fragment 이동
            when (it) {
                R.id.home -> navController.navigate(R.id.home)
                R.id.childrenList -> navController.navigate(R.id.childrenList)
                R.id.childrenInfo -> navController.navigate(R.id.childrenInfo)
                R.id.setting -> navController.navigate(R.id.setting)
            }
        }
    }

    fun hideBottomNavigation(state: Boolean){ // bottom navigation 숨기는 함수
        binding.apply {
            if (state) { // 만약 인자가 true라면
                bottomNavigation.visibility = View.GONE // bottom navigation 숨기기
            } else {
                bottomNavigation.visibility = View.VISIBLE // bottom navigation 보이게
            }
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean { // 네트워크 연결 체크
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false

        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}