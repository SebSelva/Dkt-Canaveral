package com.decathlon.canaveral.common.model

import kotlinx.parcelize.Parcelize

@Parcelize
class Player (
        override var id: Int,
        var nickname: String,
        var firstname: String,
        var lastname: String,
        var image: String?

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
