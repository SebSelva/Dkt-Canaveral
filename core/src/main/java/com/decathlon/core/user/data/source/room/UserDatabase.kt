package com.decathlon.core.user.data.source.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.decathlon.core.user.data.entity.UserEntity

@Database(
    version = 1,
    entities = [UserEntity::class],
    exportSchema = false)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    UserDatabase::class.java.simpleName
                    /*).addMigrations(
                        MIGRATION_1_2*/
                ).build()
                INSTANCE = instance
                instance
            }
        }

        /*private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("")
            }
        }*/
    }
}