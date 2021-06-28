package com.decathlon.core.player.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.decathlon.core.player.model.Player
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

@Parcelize
@Entity(tableName = "player")
data class PlayerEntity(

    @PrimaryKey(autoGenerate = true)
    @NotNull
    @ColumnInfo(name = "uid")
    var uid: Int,

    @ColumnInfo(name = "nickname")
    var nickname: String,

    @ColumnInfo(name = "firstname")
    var firstname: String,

    @ColumnInfo(name = "lastname")
    var lastname: String,

    @ColumnInfo(name = "image")
    var image: String?

) : Parcelable {
    constructor(player: Player) :this(player.id, player.nickname, player.firstname, player.lastname, player.image)
}
