package com.binktec.ocrandroid.data.model.response

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class OcrResponse (
        @PrimaryKey (autoGenerate = true) val responseId:Long?,
		val imagePath: String?,
		val name: String?,
		@SerializedName("parsedResults") val parsedResults : List<ParsedResults>,
		@SerializedName("oCRExitCode") val oCRExitCode : Int,
		@SerializedName("isErroredOnProcessing") val isErroredOnProcessing : Boolean,
		@SerializedName("errorMessage") val errorMessage : String,
		@SerializedName("errorDetails") val errorDetails : String,
		@SerializedName("searchablePDFURL") val searchablePDFURL : String,
		@SerializedName("processingTimeInMilliseconds") val processingTimeInMilliseconds : Int
)