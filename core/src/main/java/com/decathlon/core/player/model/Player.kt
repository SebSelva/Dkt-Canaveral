package com.decathlon.core.player.model

import android.os.Parcelable
import com.decathlon.core.player.data.entity.PlayerEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player (
        var id: Long,
        var nickname: String,
        var firstname: String,
        var lastname: String,
        var tempname: String?,
        var image: String?,
        var userId: Int?

) : Parcelable {
        constructor(playerEntity: PlayerEntity) : this(
                playerEntity.uid, playerEntity.nickname,
                playerEntity.firstname, playerEntity.lastname,
                playerEntity.tempname, playerEntity.image, playerEntity.userId)
}
