package com.decathlon.canaveral.common.utils

import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.PopupMenu
import com.decathlon.canaveral.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraUtils {

    companion object {

        fun getImageFile(context: Context, imageTitle: String): File {
            val cw = ContextWrapper(context)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val file = File(directory, imageTitle)
            if (file.exists()) file.delete()
            return file
        }

        fun getImageName(playerId :Long) :String {
            val dateTimeString = SimpleDateFormat("yyyyMMdd-HHmmss",
                Locale.getDefault()).format(Date())
            return "player"+playerId.toString()+"_$dateTimeString.jpeg"
        }

        fun getPictureMenu(view: View,
                                   onCameraClicked: () -> Unit,
                                   onGalleryClicked: ()-> Unit
        ): PopupMenu {
            val pictureMenu = PopupMenu(view.context, view)
            pictureMenu.menuInflater.inflate(R.menu.option_picture, pictureMenu.menu)
            pictureMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.camera -> onCameraClicked.invoke()
                    R.id.gallery -> onGalleryClicked.invoke()
                }
                true
            }
            return pictureMenu
        }
    }

}