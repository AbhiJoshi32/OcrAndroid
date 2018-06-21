package com.binktec.ocrandroid.repository

import android.arch.lifecycle.LiveData
import com.binktec.ocrandroid.AppExecutors
import com.binktec.ocrandroid.api.ApiResponse
import com.binktec.ocrandroid.api.OcrService
import com.binktec.ocrandroid.data.db.OcrRequestDao
import com.binktec.ocrandroid.data.db.OcrResponseDao
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.data.model.Resource
import com.binktec.ocrandroid.data.model.response.OcrResponse
import okhttp3.MediaType
import java.io.File

import javax.inject.Inject
import okhttp3.RequestBody
import okhttp3.MultipartBody

class OcrRepo
@Inject
constructor( val ocrRequestDao: OcrRequestDao,
             val ocrResponseDao: OcrResponseDao,
             val appExecutors: AppExecutors,
             val ocrService: OcrService){

    fun getAllOcrReq() = ocrRequestDao.findAll()

    fun getResponse(name: String,path: String): LiveData<Resource<OcrResponse>>{
        return object : NetworkBoundResource<OcrResponse, OcrResponse>(appExecutors) {
            override fun saveCallResult(item: OcrResponse) {
                ocrResponseDao.insert(item)
            }

            override fun shouldFetch(data: OcrResponse?) = data == null

            override fun loadFromDb(): LiveData<OcrResponse> = ocrResponseDao.findByReqNamePath(name,path)

            override fun createCall(): LiveData<ApiResponse<OcrResponse>> {
                val file = File(path)
                val requestFile = RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                )
                val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)
                val descriptionString = "hello, this is description speaking"
                val description = RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString)
                return ocrService.getOcrResult(description,body)
            }

        }.asLiveData()
    }
    fun getRequest(name: String,path: String) = ocrRequestDao.findByNamePath(name,path)
    fun newReq(ocrReq: OcrRequest) = appExecutors.diskIO().execute {
        ocrRequestDao.insert(ocrReq)
    }
}