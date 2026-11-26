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
        CollabMemberEntity::class,
        TaskMessageEntity::class,
        SubtaskEntity::class
    ],
    version = 5,
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

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `collab_members` ADD COLUMN `isCompleted` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `collab_members` ADD COLUMN `completedAt` INTEGER DEFAULT NULL")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                if (!db.hasColumn("tasks", "attachmentPath")) {
                    db.execSQL("ALTER TABLE `tasks` ADD COLUMN `attachmentPath` TEXT")
                }
                if (!db.hasColumn("collab_members", "isCompleted")) {
                    db.execSQL("ALTER TABLE `collab_members` ADD COLUMN `isCompleted` INTEGER NOT NULL DEFAULT 0")
                }
                if (!db.hasColumn("collab_members", "completedAt")) {
                    db.execSQL("ALTER TABLE `collab_members` ADD COLUMN `completedAt` INTEGER DEFAULT NULL")
                }
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `task_messages` (" +
                            "`id` TEXT NOT NULL, `taskId` TEXT NOT NULL, `senderId` TEXT NOT NULL, " +
                            "`senderName` TEXT NOT NULL, `message` TEXT NOT NULL, " +
                            "`createdAt` INTEGER NOT NULL, `isSystemLog` INTEGER NOT NULL, " +
                            "PRIMARY KEY(`id`), " +
                            "FOREIGN KEY(`taskId`) REFERENCES `tasks`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_task_messages_taskId` ON `task_messages` (`taskId`)"
                )
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `subtasks` (" +
                            "`id` TEXT NOT NULL, `taskId` TEXT NOT NULL, `title` TEXT NOT NULL, " +
                            "`assigneeId` TEXT, `isDone` INTEGER NOT NULL, `completedAt` INTEGER, " +
                            "`createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`), " +
                            "FOREIGN KEY(`taskId`) REFERENCES `tasks`(`id`) " +
                            "ON UPDATE NO ACTION ON DELETE CASCADE )"
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subtasks_taskId` ON `subtasks` (`taskId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_subtasks_assigneeId` ON `subtasks` (`assigneeId`)")
            }
        }

        private fun SupportSQLiteDatabase.hasColumn(table: String, column: String): Boolean {
            query("PRAGMA table_info(`$table`)").use { cursor ->
                val nameIndex = cursor.getColumnIndex("name")
                while (cursor.moveToNext()) {
                    if (cursor.getString(nameIndex) == column) return true
                }
            }
            return false
        }

        @Volatile
        private var INSTANCE: ZenTaskDatabase? = null
        fun getDatabase(context: Context): ZenTaskDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ZenTaskDatabase::class.java,
                    "zentask_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                    .also { database -> INSTANCE = database }
            }
        }
    }
}