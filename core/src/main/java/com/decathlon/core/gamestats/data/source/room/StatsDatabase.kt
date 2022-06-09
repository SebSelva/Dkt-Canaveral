package com.decathlon.core.gamestats.data.source.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import com.decathlon.core.gamestats.data.source.room.entity.GameActivityEntity

@Database(
    version = 2,
    entities = [DartsStatEntity::class, GameActivityEntity::class],
    exportSchema = false)
abstract class StatsDatabase: RoomDatabase() {

    abstract fun statsDao(): StatsDao

    abstract fun activityDao(): ActivityDao

    companion object {

        @Volatile
        private var INSTANCE: StatsDatabase? = null

        fun getDatabase(context: Context): StatsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StatsDatabase::class.java,
                    StatsDatabase::class.java.simpleName
                    /*).addMigrations(
                        MIGRATION_1_2*/
                ).build()
                INSTANCE = instance
                instance
            }
        }

        /*private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE dartStats ADD COLUMN gamesDraw INTEGER")
                database.execSQL("ALTER TABLE dartStats ADD COLUMN gamesDrawPercent INTEGER")
            }
        }*/
    }
}