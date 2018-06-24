package com.binktec.ocrandroid.data.model.response


import com.google.gson.annotations.SerializedName

data class TextOverlay (
		@SerializedName("Lines") val lines : List<Lines>?,
		@SerializedName("HasOverlay") val hasOverlay : Boolean?,
		@SerializedName("Message") val message : String?
)