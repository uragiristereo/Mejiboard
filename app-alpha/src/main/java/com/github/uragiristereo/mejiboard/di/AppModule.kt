package com.github.uragiristereo.mejiboard.di

import android.content.Context
import androidx.room.Room
import com.github.uragiristereo.mejiboard.data.local.database.AppDatabase
import com.github.uragiristereo.mejiboard.data.model.local.database.AppDatabaseMigration
import com.github.uragiristereo.mejiboard.data.repository.local.PreferencesRepository
import com.squareup.moshi.Moshi
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
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "mejiboard-database")
            .addMigrations(
                AppDatabaseMigration.MIGRATE_1_2,
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshiBuilder(): Moshi.Builder {
        return Moshi.Builder()
    }
}