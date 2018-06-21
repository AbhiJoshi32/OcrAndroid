package com.binktec.ocrandroid.data.model

import android.arch.persistence.room.Entity

@Entity (
        primaryKeys = ["imagePath","name"]
)
data class OcrRequest (
        var imagePath: String,
        var name:String,
        var time:Long
)