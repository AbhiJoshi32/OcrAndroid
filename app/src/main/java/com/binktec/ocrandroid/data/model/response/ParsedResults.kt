package com.binktec.ocrandroid.data.model.response

import com.google.gson.annotations.SerializedName

data class ParsedResults (
		@SerializedName("textOverlay") val textOverlay : TextOverlay,
		@SerializedName("fileParseExitCode") val fileParseExitCode : Int,
		@SerializedName("parsedText") val parsedText : String,
		@SerializedName("errorMessage") val errorMessage : String,
		@SerializedName("errorDetails") val errorDetails : String
)