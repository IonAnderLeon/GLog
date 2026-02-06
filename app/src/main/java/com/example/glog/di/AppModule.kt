package com.example.glog.di

import com.example.glog.data.mapper.GameMapper
import com.example.glog.data.network.api.GLogApiService
import com.example.glog.data.network.routes.K.BASE_URL
import com.example.glog.domain.repository.GameRepository
import com.example.glog.domain.repository.GameRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/*
Solo implementado las inyecciones a game, faltan el resto
 */

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGameApiService(retrofit: Retrofit): GLogApiService {
        return retrofit.create(GLogApiService::class.java)
    }

    @Provides
    fun provideGameMapper(): GameMapper = GameMapper()


    @Provides
    fun provideGameRepository(
        apiService: GLogApiService,
        gameMapper: GameMapper
    ): GameRepository {
        return GameRepositoryImpl(apiService, gameMapper)
    }



}