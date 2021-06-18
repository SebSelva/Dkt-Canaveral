package com.decathlon.core.player.data.source.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.decathlon.core.player.data.entity.PlayerEntity


@Database(entities = [PlayerEntity::class], version = 1, exportSchema = false)
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
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}