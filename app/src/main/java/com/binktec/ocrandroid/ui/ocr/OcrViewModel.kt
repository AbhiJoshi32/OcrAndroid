package com.binktec.ocrandroid.ui.ocr

import android.arch.lifecycle.*
import com.binktec.ocrandroid.data.model.*
import com.binktec.ocrandroid.repository.OcrRepo
import com.binktec.ocrandroid.repository.TextExtractorRepo
import com.binktec.ocrandroid.utils.AbsentLiveData
import java.io.File
import javax.inject.Inject

class OcrViewModel @Inject constructor(private val ocrRepo: OcrRepo, private val textExtractorRepo: TextExtractorRepo): ViewModel() {
    val reqParam = MutableLiveData<ReqParams>()
    val file = MutableLiveData<File>()

    val ocrRequest: LiveData<OcrRequest> = Transformations.switchMap(reqParam) {
        it.ifExists { name, path ->
            ocrRepo.getRequest(name,path)
        }
    }

    val response:LiveData<Resource<OcrResponse>> = Transformations.switchMap(reqParam) {
        it.ifExists { name , path ->
            ocrRepo.getResponse(name = name, path = path)
        }
    }


    val textToExtract = MutableLiveData<String>()

    val entities:LiveData<Resource<TextEntities>> = Transformations.switchMap(textToExtract) {
        if (!it.isNullOrEmpty()) {
            textExtractorRepo.getTextExtracted(reqParam.value!!.name!!, reqParam.value!!.path!!,it)
        } else AbsentLiveData.create()
    }


    fun setReqParam(name: String?, path: String?) {
        val update = ReqParams(name, path)
        if (reqParam.value == update) {
            return
        }
        reqParam.value = update
    }

    fun newReq(ocrReq: OcrRequest) {
        reqParam.value = ReqParams(ocrReq.name,ocrReq.imagePath)
        ocrRepo.newReq(ocrReq)
    }

    data class ReqParams(val name: String?, val path: String?) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (name.isNullOrBlank() || path.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                f(name!!, path!!)
            }
        }
    }
}