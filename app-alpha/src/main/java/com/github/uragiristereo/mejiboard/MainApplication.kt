package com.github.uragiristereo.mejiboard

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application(), ImageLoaderFactory {
    @Inject
    lateinit var networkRepository: NetworkRepository

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context = this)
            .diskCache(null)
            .okHttpClient { networkRepository.okHttpClient }
            .build()
    }
}