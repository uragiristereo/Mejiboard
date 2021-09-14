package com.uragiristereo.mejiboard.di

import android.content.Context
import androidx.room.Room
import com.uragiristereo.mejiboard.model.database.AppDatabase
import com.uragiristereo.mejiboard.model.preferences.PreferencesManager
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
    fun provideDataManager(@ApplicationContext context: Context) = PreferencesManager(context)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "mejiboard-database")
            .allowMainThreadQueries()
            .build()
    }
}