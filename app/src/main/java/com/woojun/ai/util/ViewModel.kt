package com.woojun.ai.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woojun.ai.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewModel : ViewModel() {
    private val apiData: MutableLiveData<AiResultList> = MutableLiveData()

    fun loadApiData() {
        val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
        val call: Call<AiResultList> = retrofitAPI.getAiResult(BuildConfig.ESNTLID, BuildConfig.AUTHKEY, 50, null, null, null)

        call.enqueue(object : Callback<AiResultList> {
            override fun onResponse(call: Call<AiResultList>, response: Response<AiResultList>) {
                if (response.isSuccessful) {
                    apiData.value = response.body()
                }
            }

            override fun onFailure(call: Call<AiResultList>, t: Throwable) {
            }
        })
    }

    fun getApiData(): LiveData<AiResultList> {
        return apiData
    }
}
