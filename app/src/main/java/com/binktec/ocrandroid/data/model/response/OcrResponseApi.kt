package com.binktec.ocrandroid.data.model.response

import com.google.gson.annotations.SerializedName

data class OcrResponseApi (
		@SerializedName("ParsedResults") val parsedResults : List<ParsedResults>?,
		@SerializedName("OCRExitCode") val oCRExitCode : Int?,
		@SerializedName("IsErroredOnProcessing") val isErroredOnProcessing : Boolean?,
		@SerializedName("ErrorMessage") val errorMessage : List<String>?,
		@SerializedName("ErrorDetails") val errorDetails : String?,
		@SerializedName("SearchablePDFURL") val searchablePDFURL : String?,
		@SerializedName("ProcessingTimeInMilliseconds") val processingTimeInMilliseconds : Int?
)