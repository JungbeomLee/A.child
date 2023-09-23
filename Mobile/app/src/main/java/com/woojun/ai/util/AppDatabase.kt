package com.woojun.ai.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(
    TypeConverter::class
)

@Database(entities = [UserInfo::class, SimilarDistanceUid::class], version = 6)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userInfoDao(): UserInfoDAO
    abstract fun findChildDao(): FindChildDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}