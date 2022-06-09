package com.decathlon.core.player.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.decathlon.core.player.model.Player
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "player")
data class PlayerEntity(

    @PrimaryKey(autoGenerate = true)
    var uid: Long,

    @ColumnInfo(name = "nickname")
    var nickname: String,

    @ColumnInfo(name = "firstname")
    var firstname: String,

    @ColumnInfo(name = "lastname")
    var lastname: String,

    @ColumnInfo(name = "tempname")
    var tempname: String?,

    @ColumnInfo(name = "image")
    var image: String?,

    @ColumnInfo(name = "userId")
    var userId: Int?

) : Parcelable {
    constructor(player: Player) :this(player.id, player.nickname,
        player.firstname, player.lastname,
        player.tempname, player.image, player.userId)
}
