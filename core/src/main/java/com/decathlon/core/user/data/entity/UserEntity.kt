package com.decathlon.core.user.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.decathlon.core.user.model.User
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "user")
data class UserEntity (

    @PrimaryKey(autoGenerate = true)
    var uid: Long,

    @ColumnInfo(name = "nickname")
    var nickname: String,

    @ColumnInfo(name = "firstname")
    var firstname: String,

    @ColumnInfo(name = "lastname")
    var lastname: String,

    @ColumnInfo(name = "image")
    var image: String?,

    @ColumnInfo(name = "accountId")
    var accountId: Int?,

    @ColumnInfo(name = "isMainUser")
    var isMainUser: Boolean

) : Parcelable {
    constructor(user: User) : this(user.uid, user.nickname, user.firstname,
        user.lastname, user.image, user.accountId, user.isMainUser)
}