package com.binktec.ocrandroid.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binktec.ocrandroid.R
import com.binktec.ocrandroid.di.Injectable
import com.binktec.ocrandroid.ui.OcrNavigationController
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

class MainFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = MainFragment()
    }
    @Inject
    lateinit var ocrNavigationController: OcrNavigationController

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainViewModel:MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MainViewModel::class.java)
        val adapter = OcrReqAdapter(context) {
            ocrNavigationController.navigateToOcr(it)
        }
        req_recycler.layoutManager = LinearLayoutManager(context)
        req_recycler.adapter = adapter
        mainViewModel.mutableReqList.observe(this, Observer {
            it?.let { it1 -> adapter.submitList(it1) }
        })
    }
}
