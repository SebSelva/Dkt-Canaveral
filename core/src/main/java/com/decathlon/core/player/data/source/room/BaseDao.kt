package com.decathlon.core.player.data.source.room

import androidx.room.*

@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: T): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAbort(item: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIgnore(item: T): Long

    @Update
    fun update(item: T)

    @Update
    fun update(items: List<T>)

    @Delete
    fun delete(item: T)

    @Delete
    fun deleteAll(items: List<T>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAllAbortable(t: List<T>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllIgnore(t: List<T>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReplace(t: List<T>)

}