package com.binktec.ocrandroid.ui

import com.binktec.ocrandroid.MainActivity
import com.binktec.ocrandroid.R
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.ui.main.MainFragment
import com.binktec.ocrandroid.ui.ocr.OcrFragment

import javax.inject.Inject

class OcrNavigationController @Inject constructor(mainActivity: MainActivity) {
    private val containerId = R.id.container
    private val toolbar = mainActivity.supportActionBar
    private val context = mainActivity

    fun navigateToMain() {
        val mainFragment = MainFragment.newInstance()
        toolbar?.title = context.getString(R.string.main_frag_title)
        context.supportFragmentManager
                .beginTransaction()
                .replace(containerId,mainFragment)
                .commitAllowingStateLoss()
    }
    fun navigateToOcr(ocrRequest: OcrRequest)  {
        val ocrFragment = OcrFragment.newInstance(ocrRequest.name,ocrRequest.imagePath)
        toolbar?.title = context.getString(R.string.ocr_frag_title)
        context.supportFragmentManager
                .beginTransaction()
                .replace(containerId,ocrFragment)
                .commitAllowingStateLoss()
    }
}