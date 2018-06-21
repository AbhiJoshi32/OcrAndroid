package com.binktec.ocrandroid.di

import android.app.Application
import android.arch.persistence.room.Room
import com.binktec.ocrandroid.api.OcrService
import com.binktec.ocrandroid.data.db.OcrDb
import com.binktec.ocrandroid.data.db.OcrRequestDao
import com.binktec.ocrandroid.data.db.OcrResponseDao
import com.binktec.ocrandroid.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideService (): OcrService {
        return Retrofit.Builder()
                .baseUrl("https://api.ocr.space")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(OcrService::class.java)

    }
    @Singleton
    @Provides
    fun provideDb(app: Application): OcrDb {
        return Room
            .databaseBuilder(app, OcrDb::class.java, "ocr.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideReqDao(db: OcrDb): OcrRequestDao{
        return db.requestDao()
    }

    @Singleton
    @Provides
    fun provideResDao(db: OcrDb): OcrResponseDao {
        return db.responseDao()
    }
}
