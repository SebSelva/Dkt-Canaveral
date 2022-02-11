package com.decathlon.canaveral.common.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.decathlon.canaveral.common.model.Player
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraUtils(private val activity: Activity?) {

    fun getImageFile(imageTitle: String): File {
        val cw = ContextWrapper(activity as Context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val file = File(directory, imageTitle)
        if (file.exists()) file.delete()
        return file
    }

    companion object {
        fun getPlayerImageName(player :Player) :String {
            val dateTimeString = SimpleDateFormat("yyyyMMdd-HHmmss",
                Locale.getDefault()).format(Date())
            return "player"+player.id.toString()+"_$dateTimeString.jpeg"
        }
    }

}