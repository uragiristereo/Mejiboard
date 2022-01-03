package com.github.uragiristereo.mejiboard.di

import android.content.Context
import androidx.room.Room
import com.github.uragiristereo.mejiboard.data.database.AppDatabase
import com.github.uragiristereo.mejiboard.data.repository.PreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context) = PreferencesRepository(context)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "mejiboard-database")
            .allowMainThreadQueries()
            .build()
    }
}