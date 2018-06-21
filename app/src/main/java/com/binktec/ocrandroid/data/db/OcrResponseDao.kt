package com.binktec.ocrandroid.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.binktec.ocrandroid.data.model.response.OcrResponse

@Dao
interface OcrResponseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(apiRequest: OcrResponse) : Long

    @Query("SELECT * FROM ocrresponse")
    fun findAll(): LiveData<List<OcrResponse>>

    @Query("SELECT * FROM ocrresponse WHERE responseId = :id")
    fun findById(id: Long): LiveData<OcrResponse>

    @Delete
    fun deleteResponse(ocrResponse: OcrResponse)

    @Query("SELECT * FROM ocrresponse WHERE name = :name AND imagePath = :path")
    fun findByReqNamePath(name: String, path: String): LiveData<OcrResponse>
}
