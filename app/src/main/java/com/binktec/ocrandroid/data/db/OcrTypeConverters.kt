package com.binktec.ocrandroid.data.db

import android.arch.persistence.room.TypeConverter
import com.binktec.ocrandroid.data.model.response.ParsedResults
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import java.util.*


object PostmanTypeConverters {
    private val gson = Gson()
    @TypeConverter
    @JvmStatic
    fun stringToSomeObjectList(data: String?): List<ParsedResults> {
        if (data == "null") {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<ParsedResults>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    @JvmStatic
    fun someObjectListToString(someObjects: List<ParsedResults>): String {
        return gson.toJson(someObjects)
    }
}