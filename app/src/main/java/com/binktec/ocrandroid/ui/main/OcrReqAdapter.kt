package com.binktec.ocrandroid.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binktec.ocrandroid.GlideApp
import com.binktec.ocrandroid.R
import com.binktec.ocrandroid.data.model.OcrRequest
import kotlinx.android.synthetic.main.ocr_req_card.view.*
import java.text.SimpleDateFormat
import java.util.*

class OcrReqAdapter(private val context: Context?, private val clickListener: (OcrRequest)->Unit) : RecyclerView.Adapter<OcrReqAdapter.ViewHolder>() {
    var dataset = emptyList<OcrRequest>()
    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val image = view.req_img
        val name = view.req_name
        val date = view.req_date
        val containerView = view
    }

    fun submitList(listOcrRequest: List<OcrRequest>) {
        dataset = listOcrRequest
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OcrReqAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ocr_req_card, parent, false))
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val req = dataset[position]
        context?.let { GlideApp.with(it).load(Uri.parse(req.imagePath)).into(holder.image) }
        val date = Date(req.time*1000L)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        holder.date.text = dateFormat.format(date)
        holder.name.text = req.name
        holder.containerView.setOnClickListener{clickListener(req)}
    }
}