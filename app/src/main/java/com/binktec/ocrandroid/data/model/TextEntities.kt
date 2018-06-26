package com.binktec.ocrandroid.data.model

import android.arch.persistence.room.Entity

@Entity (primaryKeys = ["name","imagePath"])
data class TextEntities (
        val name: String,
        val imagePath: String,
        val contactName: String,
        val number: String,
        val email: String,
        val place: String,
        val company: String)