package com.binktec.ocrandroid.di

import com.binktec.ocrandroid.ui.main.MainFragment
import com.binktec.ocrandroid.ui.ocr.OcrFragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment
    @ContributesAndroidInjector
    abstract fun contributeOcrFragment(): OcrFragment
}
