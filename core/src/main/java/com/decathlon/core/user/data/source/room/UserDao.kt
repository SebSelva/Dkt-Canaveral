package com.decathlon.core.user.data.source.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.decathlon.core.common.data.source.room.BaseDao
import com.decathlon.core.user.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao : BaseDao<UserEntity> {

    @Query("SELECT * FROM user")
    fun getUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM user WHERE isMainUser == 1")
    fun getMainUser(): Flow<UserEntity?>

    @Query("SELECT * FROM user WHERE uid LIKE :id")
    fun findUserById(id: Long): UserEntity?

    @Query("SELECT * FROM user WHERE nickname LIKE :nickname")
    fun findUsersByNickname(nickname: String?): List<UserEntity>?

    @Update
    fun updateUser(pl: UserEntity)

    @Delete
    fun deleteUser(pl: UserEntity)
}