package com.decathlon.core.user.model

import android.os.Parcelable
import com.decathlon.core.user.data.entity.UserEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    var uid: Long,
    var nickname: String,
    var firstname: String,
    var lastname: String,
    var image: String?,
    var accountId: Int?,
    var isMainUser: Boolean
): Parcelable {
    constructor(entity: UserEntity) : this(entity.uid, entity.nickname, entity.firstname,
        entity.lastname, entity.image, entity.accountId, entity.isMainUser)
    constructor(isMainUser: Boolean): this(0, "", "", "", null, null, isMainUser)
}