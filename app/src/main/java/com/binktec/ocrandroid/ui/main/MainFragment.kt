package com.binktec.ocrandroid.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binktec.ocrandroid.R
import com.binktec.ocrandroid.di.Injectable
import com.binktec.ocrandroid.ui.OcrNavigationController
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper


class MainFragment : Fragment(), Injectable {

    companion object {
        fun newInstance() = MainFragment()
    }
    @Inject
    lateinit var ocrNavigationController: OcrNavigationController

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainFragmentViewModel: MainFragmentViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: OcrReqAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainFragmentViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MainFragmentViewModel::class.java)
        if (activity != null) {
            mainViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                    .get(MainViewModel::class.java)
            mainViewModel.title.value = "Recent"
        }
        adapter = OcrReqAdapter(context) {
            ocrNavigationController.navigateToOcr(it)
        }
        val layoutManager = LinearLayoutManager(context)
        req_recycler.layoutManager = layoutManager
        req_recycler.setHasFixedSize(true)
        val itemDecoration = VerticalSpaceItemDecoration(16)
        req_recycler.addItemDecoration(itemDecoration)
        req_recycler.itemAnimator = DefaultItemAnimator()
        req_recycler.adapter = adapter
        mainFragmentViewModel.mutableReqList.observe(this, Observer {
            it?.let {
                adapter.submitList(it)
                //Show empty response if list is empty and remove if not empty
                if (it.isEmpty()) empty_ocr_list.visibility = View.VISIBLE
                else empty_ocr_list.visibility = View.GONE
            }
        })
        req_ocr.setOnClickListener {
            ocrNavigationController.navigateToOcr()
        }
        initSwipe()
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val req = adapter.removeItem(position)
                mainFragmentViewModel.removeItem(req)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(req_recycler)
    }
}

private class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        outRect.bottom = verticalSpaceHeight
    }
}
