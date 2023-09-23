package com.woojun.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.woojun.ai.databinding.ActivityIntroBinding
import com.woojun.ai.util.ProgressUtil

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!isNetworkAvailable(this)){
            ProgressUtil.createDialog( // 결과 Dialog 보여주기
                this,
                false,
                "인터넷 연결 실패",
                "인터넷 연결에 실패하였습니다\n" +
                        "A·아이를 이용하시려면 인터넷을 필요합니다\n" +
                        "다시 한번 인터넷 연결이 되었는지 확인해주세요"
            ) {
                if (isNetworkAvailable(this)) { // 인터넷에 연결되었다면
                    it.dismiss() //
                } else {
                    Toast.makeText(this, "인터넷을 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
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