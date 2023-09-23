package com.woojun.ai

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth
        Handler().postDelayed({
            if (auth.currentUser == null) { // 로그인이 안되어 있다면
                writeStringListToInternalStorage(baseContext, "search_text", arrayListOf()) // writeStringListToInternalStorage 함수 실행
                startActivity(Intent(this@SplashActivity, IntroActivity::class.java)) // IntroActivity 이동
                finishAffinity() // 액티비티 모두 지우기
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java)) // MainActivity 이동
                finishAffinity() // 액티비티 모두 지우기
            }
        }, 2000)
    }

    private fun writeStringListToInternalStorage(context: Context, filename: String, stringList: ArrayList<String>) {
        try { // 파일에 쓰기
            val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val fileContent = stringList.joinToString("\n")
            outputStream.write(fileContent.toByteArray())
            outputStream.close()
        } catch (e: Exception) { // 오류 발생 처리
            Toast.makeText(context, "오류 발생 앱을 삭제한 후 다시 설치해주세요", Toast.LENGTH_SHORT).show()
        }
    }
}