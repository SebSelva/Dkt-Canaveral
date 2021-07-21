package com.decathlon.core.player.model

import android.os.Parcelable
import com.decathlon.core.player.data.entity.PlayerEntity
import kotlinx.parcelize.Parcelize

@Parcelize
class Player (
        var id: Int,
        var nickname: String,
        var firstname: String,
        var lastname: String,
        var image: String?

) : Parcelable {
        constructor(playerEntity: PlayerEntity) : this(playerEntity.uid, playerEntity.nickname,
                playerEntity.firstname, playerEntity.lastname, playerEntity.image)
}
