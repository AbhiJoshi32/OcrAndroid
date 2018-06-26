package com.binktec.ocrandroid.api

import android.arch.lifecycle.LiveData
import com.binktec.ocrandroid.data.model.TextExtractorApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TextExtractorService {
    @FormUrlEncoded
    @POST("/")
    fun getEntity(@Field("text") text:String,
                  @Field("extractors") extractor:String ="entities")
            :LiveData<ApiResponse<TextExtractorApiResponse>>
}