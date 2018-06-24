package com.binktec.ocrandroid

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.binktec.ocrandroid.ui.OcrNavigationController
import com.binktec.ocrandroid.ui.main.MainViewModel
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.main_activity.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity(),HasSupportFragmentInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var ocrNavigationController: OcrNavigationController
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel: MainViewModel

    override fun supportFragmentInjector()= dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MainViewModel::class.java)
        mainViewModel.title.observe(this, Observer {
            Timber.d( "observed %s", it)
            if (!it.isNullOrBlank())
                supportActionBar?.title  = it
        })
        if (savedInstanceState == null) {
            ocrNavigationController.navigateToMain()
        }
    }
}
