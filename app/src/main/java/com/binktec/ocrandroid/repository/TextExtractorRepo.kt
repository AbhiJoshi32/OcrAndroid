package com.binktec.ocrandroid.repository

import android.arch.lifecycle.LiveData
import com.binktec.ocrandroid.AppExecutors
import com.binktec.ocrandroid.api.TextExtractorService
import com.binktec.ocrandroid.data.db.TextExtractorDao
import com.binktec.ocrandroid.data.model.Resource
import com.binktec.ocrandroid.data.model.TextEntities
import com.binktec.ocrandroid.data.model.TextExtractorApiResponse
import com.binktec.ocrandroid.utils.CardParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextExtractorRepo
@Inject
constructor(private val textExtractorService: TextExtractorService,
            private val appExecutors: AppExecutors,
            private val textExtractorDao: TextExtractorDao) {

    fun getTextExtracted(name:String,path:String,text:String):LiveData<Resource<TextEntities>> {
        return object : NetworkBoundResource<TextEntities,TextExtractorApiResponse>(appExecutors) {
            override fun saveCallResult(item: TextExtractorApiResponse) {
                val entities = item.response?.entities
                val parser = CardParser(text,entities.orEmpty())
                parser.parse()
                val textEntities = TextEntities(name,path,parser.name,parser.number,parser.email,parser.place,parser.company)
                textExtractorDao.insert(textEntities)
            }

            override fun shouldFetch(data: TextEntities?) = data == null

            override fun loadFromDb() = textExtractorDao.findByImageNamePath(name,path)

            override fun createCall() = textExtractorService.getEntity(text)

        }.asLiveData()
    }
}