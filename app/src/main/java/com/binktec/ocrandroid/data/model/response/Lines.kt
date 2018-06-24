package com.binktec.ocrandroid.data.model.response

import com.google.gson.annotations.SerializedName

data class Lines (
		@SerializedName("Words") val words : List<Words>?,
		@SerializedName("MaxHeight") val maxHeight : Int?,
		@SerializedName("MinTop") val minTop : Int?
)