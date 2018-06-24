package com.binktec.ocrandroid.ui

import com.binktec.ocrandroid.MainActivity
import com.binktec.ocrandroid.R
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.ui.main.MainFragment
import com.binktec.ocrandroid.ui.ocr.OcrFragment

import javax.inject.Inject

class OcrNavigationController @Inject constructor(mainActivity: MainActivity) {
    private val containerId = R.id.container
    private val context = mainActivity

    fun navigateToMain() {
        val mainFragment = MainFragment.newInstance()
        context.supportFragmentManager
                .beginTransaction()
                .replace(containerId,mainFragment)
                .commitAllowingStateLoss()
    }
    fun navigateToOcr(ocrRequest: OcrRequest)  {
        val ocrFragment = OcrFragment.newInstance(ocrRequest.name,ocrRequest.imagePath)
        context.supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(containerId,ocrFragment)
                .commitAllowingStateLoss()
    }

    fun navigateToOcr()  {
        val ocrFragment = OcrFragment.newInstance(null,null)
        context.supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(containerId,ocrFragment)
                .commitAllowingStateLoss()
    }
}