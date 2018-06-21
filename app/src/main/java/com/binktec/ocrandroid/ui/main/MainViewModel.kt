package com.binktec.ocrandroid.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.repository.OcrRepo
import javax.inject.Inject

class MainViewModel @Inject constructor(val ocrRepo: OcrRepo): ViewModel() {
    val mutableReqList = ocrRepo.getAllOcrReq()
}
