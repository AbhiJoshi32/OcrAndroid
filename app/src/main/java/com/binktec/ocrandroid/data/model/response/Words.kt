package com.binktec.ocrandroid.data.model.response


import com.google.gson.annotations.SerializedName

data class Words (
		@SerializedName("WordText") val wordText : String?,
		@SerializedName("Left") val left : Int?,
		@SerializedName("Top") val top : Int?,
		@SerializedName("Height") val height : Int?,
		@SerializedName("Width") val width : Int?
)