package com.decathlon.core.user.data.source

import android.content.Context
import com.decathlon.core.user.data.entity.UserEntity
import com.decathlon.core.user.data.source.room.UserDatabase
import com.decathlon.core.user.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class RoomUserDataSource(context: Context) : UserDataSource {

    private val userDao = UserDatabase.getDatabase(context).userDao()

    override fun getUsers() =
        userDao.getUsers().map { it.map { userEntity -> User(userEntity) } }

    override fun getMainUser(): Flow<User?> = flow {
        emit(userDao.getMainUser().first()?.let { entity -> User(entity) })
    }

    override suspend fun insertUser(user: User) {
        userDao.insert(UserEntity(user))
    }

    override suspend fun removeUser(user: User): Unit =
        getEntityById(user.uid).let {
            it?.let { userDao.deleteUser(it) }
        }

    override suspend fun getEntityById(id: Long): UserEntity? =
        userDao.findUserById(id)

    override suspend fun getUserByNickname(nickname: String) =
        userDao.findUsersByNickname(nickname)?.map {entity -> User(entity) }

    override suspend fun updateUser(user: User) =
        userDao.updateUser(UserEntity(user))
}