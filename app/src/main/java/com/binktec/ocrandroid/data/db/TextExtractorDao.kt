package com.binktec.ocrandroid.data.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.binktec.ocrandroid.data.model.TextEntities

@Dao
interface TextExtractorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entities: TextEntities)

    @Query("SELECT * FROM textentities")
    fun findAll(): LiveData<List<TextEntities>>

    @Delete
    fun deleteEntity(ocrResponse: TextEntities)

    @Query("SELECT * FROM textentities WHERE name = :name and imagePath = :path")
    fun findByImageNamePath(name: String, path: String): LiveData<TextEntities>


    @Query("SELECT * FROM textentities WHERE name = :name and imagePath = :path")
    fun findByImageNamePathSync(name: String, path: String): TextEntities?
}