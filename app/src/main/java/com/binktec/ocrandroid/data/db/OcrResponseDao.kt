package com.binktec.ocrandroid.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.binktec.ocrandroid.data.model.OcrResponse

@Dao
interface OcrResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(apiRequest: OcrResponse)

    @Query("SELECT * FROM ocrresponse")
    fun findAll(): LiveData<List<OcrResponse>>

    @Delete
    fun deleteResponse(ocrResponse: OcrResponse)

    @Query("SELECT * FROM ocrresponse WHERE name = :name and imagePath = :path")
    fun findByReqNamePath(name: String, path: String): LiveData<OcrResponse>


    @Query("SELECT * FROM ocrresponse WHERE name = :name and imagePath = :path")
    fun findByReqNamePathSync(name: String, path: String): OcrResponse?
}