package com.example.xbiztask2.ui.notifications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.xbiztask2.model.PinCodeResponse
import com.example.xbiztask2.model.PinCodeResponseItem
import com.example.xbiztask2.networkCall.ApiClient
import com.example.xbiztask2.networkCall.ApiInterface
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
    var getPinStates: MutableLiveData<List<PinCodeResponseItem>> = MutableLiveData()
    fun getPinCode(pinCode: String) {
        val apiClient: ApiInterface? = ApiClient.createService(ApiInterface::class.java)
        apiClient?.getPinStates(pinCode)?.enqueue(object :
            Callback<List<PinCodeResponseItem>> {
            override fun onResponse(
                call: Call<List<PinCodeResponseItem>>,
                response: Response<List<PinCodeResponseItem>>,
            ) {

                if (response.isSuccessful) {
                    val serverResponse = Gson().toJson(response.body())
                    val myCustomResponse = "{\"pinCodeResponse\":$serverResponse}"
                    var pinCodeResponseItem = Gson().fromJson(myCustomResponse, PinCodeResponse::class.java)
                    getPinStates.postValue(pinCodeResponseItem.pinCodeResponse)
                } else {
                    val serverResponse = Gson().toJson(response.body())
                    val myCustomResponse = "{\"pinCodeResponse\":$serverResponse}"
                    var pinCodeResponseItem = Gson().fromJson(myCustomResponse, PinCodeResponse::class.java)
                    pinCodeResponseItem.pinCodeResponse[0].status = "Error"
                    pinCodeResponseItem.pinCodeResponse[0].message = "No Result Found"
                    getPinStates.postValue(pinCodeResponseItem.pinCodeResponse)
                }
            }

            override fun onFailure(call: Call<List<PinCodeResponseItem>>, t: Throwable) {
                val list: List<PinCodeResponseItem> = ArrayList()
                val pinCodeResponse: PinCodeResponse = PinCodeResponse(list)
                getPinStates.postValue(pinCodeResponse.pinCodeResponse)
            }
        })
    }
}