package com.binktec.ocrandroid.di

import android.app.Application
import android.arch.persistence.room.Room
import com.binktec.ocrandroid.api.OcrService
import com.binktec.ocrandroid.api.TextExtractorService
import com.binktec.ocrandroid.data.db.OcrDb
import com.binktec.ocrandroid.data.db.OcrRequestDao
import com.binktec.ocrandroid.data.db.OcrResponseDao
import com.binktec.ocrandroid.data.db.TextExtractorDao
import com.binktec.ocrandroid.utils.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named


@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideOcrService (@Named("ocr_service_client")client: OkHttpClient): OcrService {
        return Retrofit.Builder()
                .baseUrl("https://api.ocr.space")
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .client(client)
                .build()
                .create(OcrService::class.java)

    }

    @Singleton
    @Provides
    fun provideTextService(@Named("text_service_client")client: OkHttpClient): TextExtractorService {
        return Retrofit.Builder()
                .baseUrl("https://api.textrazor.com")
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .client(client)
                .build()
                .create(TextExtractorService::class.java)
    }

    @Singleton
    @Provides
    @Named("ocr_service_client")
    fun provideOkHttpClient() : OkHttpClient{
        val inc = Interceptor {
            val request = it.request().newBuilder()
                    .addHeader("apiKey","f97102b50788957")
                    .build()
             it.proceed(request)
        }
        val builder = OkHttpClient.Builder()
        builder.interceptors().add(inc)
        return builder.build()
    }

    @Singleton
    @Provides
    @Named("text_service_client")
    fun provideTextServiceOkHttpClient() : OkHttpClient{
        val inc = Interceptor {
            val request = it.request().newBuilder()
                    .addHeader("x-textrazor-key","3f4df9cade96977f285287bf5c83dc150214c75faf8f963324b05e93")
                    .build()
            it.proceed(request)
        }
        val builder = OkHttpClient.Builder()
        builder.interceptors().add(inc)
        return builder.build()
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
    fun provideReqDao(db: OcrDb): OcrRequestDao {
        return db.requestDao()
    }

    @Singleton
    @Provides
    fun provideResDao(db: OcrDb): OcrResponseDao {
        return db.responseDao()
    }

    @Singleton
    @Provides
    fun provideTextExtratorDao(db: OcrDb): TextExtractorDao {
        return db.textExtractorDao()
    }
}
