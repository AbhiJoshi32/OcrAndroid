package com.binktec.ocrandroid.data.model.response

import com.google.gson.annotations.SerializedName

data class Lines (

		@SerializedName("words") val words : List<Words>,
		@SerializedName("maxHeight") val maxHeight : Int,
		@SerializedName("minTop") val minTop : Int
)