package com.example.zentask.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
        CategoryEntity::class,
        CollabInviteEntity::class,
        CollabMemberEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ZenTaskDatabase : RoomDatabase() {

    // =============================================
    // DATA ACCESS OBJECTS
    // =============================================

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun collabDao(): CollabDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `collab_invites` (" +
                            "`code` TEXT NOT NULL, `taskId` TEXT NOT NULL, `ownerId` TEXT NOT NULL, " +
                            "`createdAt` INTEGER NOT NULL, `expiresAt` INTEGER NOT NULL, " +
                            "`revoked` INTEGER NOT NULL, PRIMARY KEY(`code`), " +
                            "FOREIGN KEY(`taskId`) REFERENCES `tasks`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_collab_invites_taskId` " +
                            "ON `collab_invites` (`taskId`)"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `collab_members` (" +
                            "`taskId` TEXT NOT NULL, `userId` TEXT NOT NULL, " +
                            "`joinedAt` INTEGER NOT NULL, `role` TEXT NOT NULL, " +
                            "PRIMARY KEY(`taskId`, `userId`), " +
                            "FOREIGN KEY(`taskId`) REFERENCES `tasks`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_collab_members_userId` " +
                            "ON `collab_members` (`userId`)"
                )
            }
        }

        @Volatile
        private var INSTANCE: ZenTaskDatabase? = null

        /**
         * Returns the existing database instance,
         * or creates one when first requested.
         *
         * Using applicationContext prevents
         * leaking an Activity context.
         */
        fun getDatabase(context: Context): ZenTaskDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ZenTaskDatabase::class.java,
                    "zentask_database"
                )
                    .addMigrations(MIGRATION_1_2)   // baru
                    .build()
                    .also { database -> INSTANCE = database }
            }
        }
    }
}
