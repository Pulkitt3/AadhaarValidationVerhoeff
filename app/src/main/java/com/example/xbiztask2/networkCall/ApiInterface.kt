package com.example.xbiztask2.networkCall

import com.example.xbiztask2.model.PinCodeResponse
import com.example.xbiztask2.model.PinCodeResponseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {
    @GET("pincode/{pinCode}")
    fun getPinStates(@Path("pinCode") pinCode: String): Call<List<PinCodeResponseItem>>?

}