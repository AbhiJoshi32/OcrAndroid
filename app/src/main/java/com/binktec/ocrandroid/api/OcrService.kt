package com.binktec.ocrandroid.api

import android.arch.lifecycle.LiveData
import com.binktec.ocrandroid.data.model.response.OcrResponseApi
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrService {
    @Multipart
    @POST("parse/image")
    fun getOcrResult(@Part file: MultipartBody.Part): LiveData<ApiResponse<OcrResponseApi>>

    @Multipart
    @POST("parse/image")
    fun testOcrResult(@Part file: MultipartBody.Part): Call<String>
}