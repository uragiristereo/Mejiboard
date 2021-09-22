package com.uragiristereo.mejiboard.di

import android.content.Context
import coil.util.CoilUtils
import com.uragiristereo.mejiboard.model.network.NetworkInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context: Context) = OkHttpClient.Builder()
        .cache(CoilUtils.createDefaultCache(context))
        .build()

    @Provides
    @Singleton
    fun provideNetworkInstance(@ApplicationContext context: Context, bootstrapOkHttpClient: OkHttpClient) = NetworkInstance(context, bootstrapOkHttpClient)
}