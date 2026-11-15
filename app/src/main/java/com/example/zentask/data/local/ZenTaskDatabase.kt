package com.example.zentask.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Main local database for the ZenTask application.
 *
 * Stores users, tasks, and categories in SQLite
 * through Jetpack Room.
 *
 * All task and category queries must be scoped
 * to the currently authenticated user's ID.
 */
@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZenTaskDatabase : RoomDatabase() {

    // =============================================
    // DATA ACCESS OBJECTS
    // =============================================

    abstract fun userDao(): UserDao

    abstract fun taskDao(): TaskDao

    abstract fun categoryDao(): CategoryDao

    companion object {

        // =========================================
        // SINGLETON DATABASE INSTANCE
        // =========================================

        @Volatile
        private var INSTANCE: ZenTaskDatabase? = null

        /**
         * Returns the existing database instance,
         * or creates one when first requested.
         *
         * Using applicationContext prevents
         * leaking an Activity context.
         */
        fun getDatabase(
            context: Context
        ): ZenTaskDatabase {

            return INSTANCE ?: synchronized(this) {

                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ZenTaskDatabase::class.java,
                    "zentask_database"
                )
                    // Keep database operations off
                    // the main UI thread.
                    .build()
                    .also { database ->
                        INSTANCE = database
                    }
            }
        }
    }
}
