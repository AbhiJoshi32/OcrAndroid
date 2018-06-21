package com.binktec.ocrandroid.data.model.response


import com.google.gson.annotations.SerializedName

data class TextOverlay (
		@SerializedName("lines") val lines : List<Lines>,
		@SerializedName("hasOverlay") val hasOverlay : Boolean,
		@SerializedName("message") val message : String
)