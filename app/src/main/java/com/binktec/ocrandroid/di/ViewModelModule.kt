package com.binktec.ocrandroid.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.binktec.ocrandroid.ui.main.MainFragmentViewModel
import com.binktec.ocrandroid.ui.main.MainViewModel
import com.binktec.ocrandroid.ui.ocr.OcrViewModel

import com.claritusconsulting.postman.viewmodel.OcrViewModelFactory

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainFragmentViewModel::class)
    abstract fun bindMainFragmentViewModel(mainFragmentViewModel: MainFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OcrViewModel::class)
    abstract fun bindOcrViewModel(ocrViewModel: OcrViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: OcrViewModelFactory): ViewModelProvider.Factory
}
