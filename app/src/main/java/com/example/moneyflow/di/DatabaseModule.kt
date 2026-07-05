package com.example.moneyflow.di

import android.content.Context
import androidx.room.Room
import com.example.moneyflow.data.local.FinlyDataStore
import com.example.moneyflow.data.local.FinlyDatabase
import com.example.moneyflow.data.local.dao.CycleDao
import com.example.moneyflow.data.local.dao.FinlyDao
import com.example.moneyflow.data.local.dao.NotificationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinlyDatabase {
        return Room.databaseBuilder(
            context,
            FinlyDatabase::class.java,
            "finly_db"
        ).addMigrations(
            FinlyDatabase.MIGRATION_1_2, 
            FinlyDatabase.MIGRATION_2_3, 
            FinlyDatabase.MIGRATION_3_4, 
            FinlyDatabase.MIGRATION_4_5, 
            FinlyDatabase.MIGRATION_5_6,
            FinlyDatabase.MIGRATION_6_7
        ).build()
    }

    @Provides
    fun provideDao(database: FinlyDatabase): FinlyDao {
        return database.finlyDao()
    }

    @Provides
    fun provideCycleDao(database: FinlyDatabase): CycleDao {
        return database.cycleDao()
    }

    @Provides
    fun provideNotificationDao(database: FinlyDatabase): NotificationDao {
        return database.notificationDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): FinlyDataStore {
        return FinlyDataStore(context)
    }
}
