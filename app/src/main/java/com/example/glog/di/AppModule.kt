package com.example.glog.di

import com.example.glog.data.network.common.K.BASE_URL
import com.example.glog.data.network.api.GameApiService
import com.example.glog.data.network.api.RetrofitClient
import com.example.glog.domain.repository.GameRepository
import com.example.glog.data.repository.GameRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideGameRepository(apiService: GameApiService): GameRepository {
        return GameRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideGameApiService(): GameApiService {
        return RetrofitClient.createGameApiService()
    }

}