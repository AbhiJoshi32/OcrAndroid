package com.binktec.ocrandroid.api

import android.arch.lifecycle.LiveData
import com.binktec.ocrandroid.data.model.response.OcrResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface OcrService {
    @Multipart
    @POST("parse/image")
    fun getOcrResult(@Part("description") body:RequestBody,
                     @Part file: MultipartBody.Part): LiveData<ApiResponse<OcrResponse>>
}