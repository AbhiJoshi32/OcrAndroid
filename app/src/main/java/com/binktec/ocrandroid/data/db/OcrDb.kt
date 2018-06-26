/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.binktec.ocrandroid.data.db


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.binktec.ocrandroid.data.model.OcrRequest
import com.binktec.ocrandroid.data.model.OcrResponse
import com.binktec.ocrandroid.data.model.TextEntities

/**
 * Main database description.
 */
@Database(
    entities = [
        OcrRequest::class,
        OcrResponse::class,
        TextEntities::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(OcrTypeConverters::class)
abstract class OcrDb : RoomDatabase() {
    abstract fun requestDao(): OcrRequestDao
    abstract fun responseDao(): OcrResponseDao
    abstract fun textExtractorDao(): TextExtractorDao
}
