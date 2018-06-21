package com.binktec.ocrandroid.ui.ocr

import android.arch.lifecycle.*
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.data.model.Resource
import com.binktec.ocrandroid.data.model.response.OcrResponse
import com.binktec.ocrandroid.repository.OcrRepo
import com.binktec.ocrandroid.utils.AbsentLiveData
import java.io.File
import javax.inject.Inject

class OcrViewModel @Inject constructor(val ocrRepo: OcrRepo): ViewModel() {
    var reqParam = MutableLiveData<ReqParams>()
    var file = MutableLiveData<File>()
    var ocrRequest: LiveData<OcrRequest> = Transformations.switchMap(reqParam) {
        it.ifExists { name, path ->
            ocrRepo.getRequest(name,path)
        }
    }

    val response:LiveData<Resource<OcrResponse>> = Transformations.switchMap(reqParam) {
        it.ifExists { name , path ->
            ocrRepo.getResponse(name, path)
        }
    }

    fun setReqParam(name: String?, path: String?) {
        val update = ReqParams(name, path)
        if (reqParam.value == update) {
            return
        }
        reqParam.value = update
    }

    fun newReq(ocrReq: OcrRequest) {
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