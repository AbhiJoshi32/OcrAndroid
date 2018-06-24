package com.binktec.ocrandroid.data.model.response

import com.google.gson.annotations.SerializedName

data class ParsedResults (
		@SerializedName("TextOverlay") val textOverlay : TextOverlay?,
		@SerializedName("FileParseExitCode") val fileParseExitCode : Int?,
		@SerializedName("ParsedText") val parsedText : String?,
		@SerializedName("ErrorMessage") val errorMessage : String?,
		@SerializedName("ErrorDetails") val errorDetails : String?
)