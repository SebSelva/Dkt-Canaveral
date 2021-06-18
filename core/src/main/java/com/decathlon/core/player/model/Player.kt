package com.decathlon.core.player.model

import com.decathlon.core.player.data.entity.PlayerEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
class Player (
        override var id: Int,
        var nickname: String,
        var firstname: String,
        var lastname: String,
        var image: String

) : BaseItem() {
        constructor(playerEntity: PlayerEntity) : this(playerEntity.uid, playerEntity.nickname,
                playerEntity.firstname, playerEntity.lastname, playerEntity.image)
}