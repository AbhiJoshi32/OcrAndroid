package com.binktec.ocrandroid.repository

import android.arch.lifecycle.LiveData
import com.binktec.ocrandroid.AppExecutors
import com.binktec.ocrandroid.api.ApiResponse
import com.binktec.ocrandroid.api.OcrService
import com.binktec.ocrandroid.data.db.OcrDb
import com.binktec.ocrandroid.data.db.OcrRequestDao
import com.binktec.ocrandroid.data.db.OcrResponseDao
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.data.model.OcrResponse
import com.binktec.ocrandroid.data.model.Resource
import com.binktec.ocrandroid.data.model.response.OcrResponseApi
import okhttp3.MediaType
import java.io.File

import javax.inject.Inject
import okhttp3.RequestBody
import okhttp3.MultipartBody
import javax.inject.Singleton

@Singleton
class OcrRepo
@Inject
constructor(private val ocrRequestDao: OcrRequestDao,
            private val ocrResponseDao: OcrResponseDao,
            private val appExecutors: AppExecutors,
            private val ocrService: OcrService,
            private val ocrDb: OcrDb){

    fun getAllOcrReq() = ocrRequestDao.findAll()

    fun getResponse(name: String,path: String): LiveData<Resource<OcrResponse>>{
        val file = File(path)
        val requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                file
        )
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return object : NetworkBoundResource<OcrResponse, OcrResponseApi>(appExecutors) {
            override fun saveCallResult(item: OcrResponseApi) {
                var txt = ""
                for (res in item.parsedResults.orEmpty()) {
                    txt += res.parsedText
                }
                val ocrResponse = OcrResponse(imagePath = path,name = name, resultTxt = txt)
                ocrResponseDao.insert(ocrResponse)
            }
            override fun shouldFetch(data: OcrResponse?) = data == null
            override fun loadFromDb(): LiveData<OcrResponse> = ocrResponseDao.findByReqNamePath(name = name,path = path)
            override fun createCall(): LiveData<ApiResponse<OcrResponseApi>> {
                return ocrService.getOcrResult(body)
            }
        }.asLiveData()
    }

    fun getRequest(name: String,path: String) = ocrRequestDao.findByNamePath(name,path)

    fun newReq(ocrReq: OcrRequest) = appExecutors.diskIO().execute {
        ocrRequestDao.insert(ocrReq)
    }

    fun removeReq(req: OcrRequest) {
        appExecutors.diskIO().execute {
            ocrDb.runInTransaction{
                val resp = ocrResponseDao.findByReqNamePathSync(req.name,req.imagePath)
                ocrRequestDao.deleteRequest(req)
                if (resp != null) ocrResponseDao.deleteResponse(resp)
            }
        }
    }
}