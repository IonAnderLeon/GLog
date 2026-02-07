package com.example.glog.di

import com.example.glog.data.mapper.CollectionMapper
import com.example.glog.data.mapper.GameMapper
import com.example.glog.data.mapper.RegisterMapper
import com.example.glog.data.network.api.GLogApiService
import com.example.glog.data.network.routes.K.BASE_URL
import com.example.glog.domain.repository.CollectionRepository
import com.example.glog.domain.repository.CollectionRepositoryImpl
import com.example.glog.domain.repository.GameRepository
import com.example.glog.domain.repository.GameRepositoryImpl
import com.example.glog.domain.repository.RegisterRepository
import com.example.glog.domain.repository.RegisterRepositoryImpl
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

    @Provides
    fun provideRegisterMapper(): RegisterMapper = RegisterMapper()

    @Provides
    fun provideRegisterRepository(
        apiService: GLogApiService,
        registerMapper: RegisterMapper
    ): RegisterRepository {
        return RegisterRepositoryImpl(apiService, registerMapper)
    }

    @Provides
    fun provideCollectionMapper(gameMapper: GameMapper): CollectionMapper =
        CollectionMapper(gameMapper)

    @Provides
    fun provideCollectionRepository(
        apiService: GLogApiService,
        collectionMapper: CollectionMapper
    ): CollectionRepository {
        return CollectionRepositoryImpl(apiService, collectionMapper)
    }
}