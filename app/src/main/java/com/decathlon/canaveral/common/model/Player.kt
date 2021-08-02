package com.decathlon.canaveral.common.model

import kotlinx.parcelize.Parcelize

@Parcelize
open class Player (
        override var id: Int,
        open var nickname: String,
        open var firstname: String,
        open var lastname: String,
        open var image: String?

) : BaseItem() {
        constructor(player: com.decathlon.core.player.model.Player) : this(player.id, player.nickname,
                player.firstname, player.lastname, player.image)

        fun toCore(): com.decathlon.core.player.model.Player {
                return com.decathlon.core.player.model.Player(id, nickname, firstname, lastname, image)
        }

        override fun equals(other: Any?): Boolean {
                if (other !is Player) return false
                if (other.id != this.id) return false
                if (other.nickname != this.nickname) return false
                return true
        }

        override fun hashCode(): Int {
                var result = id
                result = 31 * result + nickname.hashCode()
                result = 31 * result + firstname.hashCode()
                result = 31 * result + lastname.hashCode()
                result = 31 * result + (image?.hashCode() ?: 0)
                return result
        }
}

@Parcelize
class X01Player(
        override var id: Int,
        override var nickname: String,
        override var firstname: String,
        override var lastname: String,
        override var image: String?,
        var remainingPoints: Int,
        var checkout: Int,
        var ppd: Float
): Player(id, nickname, firstname, lastname, image)
