package com.binktec.ocrandroid.data.model

import android.arch.persistence.room.Entity

@Entity(
        primaryKeys = ["imagePath","name"]
)
data class OcrResponse (
        val imagePath: String,
        val name: String,
        val resultTxt: String
)