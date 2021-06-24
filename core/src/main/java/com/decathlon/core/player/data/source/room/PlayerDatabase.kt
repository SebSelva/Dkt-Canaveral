package com.decathlon.core.player.data.source.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.decathlon.core.player.data.entity.PlayerEntity


@Database(
    version = 1,
    entities = [PlayerEntity::class],
    exportSchema = false)
abstract class PlayerDatabase : RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    companion object {

        @Volatile
        private var INSTANCE: PlayerDatabase? = null

        fun getDatabase(context: Context): PlayerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlayerDatabase::class.java,
                    PlayerDatabase::class.java.simpleName
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