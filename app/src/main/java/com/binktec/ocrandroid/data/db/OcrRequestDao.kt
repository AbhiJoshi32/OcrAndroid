package com.binktec.ocrandroid.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.binktec.ocrandroid.data.model.OcrRequest

@Dao
interface OcrRequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(apiRequest: OcrRequest) : Long

    @Query("SELECT * FROM ocrrequest")
    fun findAll(): LiveData<List<OcrRequest>>

    @Query("SELECT * FROM ocrrequest WHERE name = :name AND imagePath = :path")
    fun findByNamePath(name: String, path:String): LiveData<OcrRequest>

    @Delete
    fun deleteRequest(request: OcrRequest)
}
