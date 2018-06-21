package com.binktec.ocrandroid.data.model.response


import com.google.gson.annotations.SerializedName

data class Words (
		@SerializedName("wordText") val wordText : String,
		@SerializedName("left") val left : Int,
		@SerializedName("top") val top : Int,
		@SerializedName("height") val height : Int,
		@SerializedName("width") val width : Int
)