package com.decathlon.core.user.data.source

import com.decathlon.core.user.data.entity.UserEntity
import com.decathlon.core.user.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    fun getUsers(): Flow<List<User>>

    fun getMainUser(): User?

    suspend fun insertUser(user: User)

    suspend fun removeUser(user: User)

    suspend fun getEntityById(id: Long): UserEntity?

    suspend fun getUserByNickname(nickname: String): List<User>?

    suspend fun updateUser(user: User)
}