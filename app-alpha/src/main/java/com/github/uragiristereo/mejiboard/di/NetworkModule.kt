package com.github.uragiristereo.mejiboard.di

import android.content.Context
import com.github.uragiristereo.mejiboard.common.util.CacheUtil
import com.github.uragiristereo.mejiboard.data.repository.DownloadRepository
import com.github.uragiristereo.mejiboard.data.repository.remote.ProvidersRepositoryImpl
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import com.github.uragiristereo.mejiboard.domain.repository.remote.ProvidersRepository
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(CacheUtil.createDefaultCache(context, "image_cache"))
            .build()
    }

    @Provides
    @Singleton
    fun provideNetworkRepository(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
    ): NetworkRepository {
        return NetworkRepository(
            context = context,
            bootstrapOkHttpClient = okHttpClient,
        )
    }

    @Provides
    @Singleton
    fun provideDownloadRepository(): DownloadRepository = DownloadRepository()

    @Provides
    @Singleton
    fun providesExoPlayerCache(@ApplicationContext context: Context): SimpleCache {
        return SimpleCache(
            File(context.cacheDir, "video_cache"),
            LeastRecentlyUsedCacheEvictor(1024 * 1024 * 256L),
            StandaloneDatabaseProvider(context)
        )
    }

    @Provides
    @Singleton
    fun provideProviderRepository(
        networkRepository: NetworkRepository,
    ): ProvidersRepository {
        return ProvidersRepositoryImpl(networkRepository)
    }
}
