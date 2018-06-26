package com.binktec.ocrandroid.data.db

import android.arch.persistence.room.TypeConverter
import com.binktec.ocrandroid.data.model.response.ParsedResults
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.util.*


object OcrTypeConverters {
    private val gson = Gson()
    @TypeConverter
    @JvmStatic
    fun stringToMap(data: String?): Map<String,String> {
        if (data == "null") {
            return Collections.emptyMap()
        }

        val mapType = object : TypeToken<Map<String,String>>() {}.type
        return gson.fromJson(data, mapType)
    }

    @TypeConverter
    @JvmStatic
    fun mapToString(someObjects: Map<String,String>): String {
        return gson.toJson(someObjects)
    }
}