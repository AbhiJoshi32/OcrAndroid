package com.binktec.ocrandroid.ui.main

import android.arch.lifecycle.ViewModel
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.repository.OcrRepo
import javax.inject.Inject

class MainFragmentViewModel @Inject constructor(private val ocrRepo: OcrRepo): ViewModel() {
    fun removeItem(req: OcrRequest) {
        ocrRepo.removeReq(req)
    }

    val mutableReqList = ocrRepo.getAllOcrReq()
}
