package com.github.uragiristereo.mejiboard.di

import android.content.Context
import coil.util.CoilUtils
import com.github.uragiristereo.mejiboard.model.network.NetworkInstance
import com.github.uragiristereo.mejiboard.model.network.download.DownloadRepository
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context: Context) = OkHttpClient.Builder()
        .cache(CoilUtils.createDefaultCache(context))
        .cache(Cache(File(context.cacheDir, "video_cache"), 1024 * 1024 * 256L))
        .build()

    @Provides
    @Singleton
    fun provideNetworkInstance(@ApplicationContext context: Context, bootstrapOkHttpClient: OkHttpClient) = NetworkInstance(context, bootstrapOkHttpClient)

    @Provides
    @Singleton
    fun provideDownloadRepository(): DownloadRepository = DownloadRepository()

    @Provides
    @Singleton
    fun providesExoPlayerCache(@ApplicationContext context: Context): SimpleCache =
        SimpleCache(
            File(context.cacheDir, "video_cache"),
            LeastRecentlyUsedCacheEvictor(1024 * 1024 * 256L),
            StandaloneDatabaseProvider(context)
        )

}