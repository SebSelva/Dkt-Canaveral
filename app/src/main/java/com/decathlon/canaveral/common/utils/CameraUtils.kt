package com.decathlon.canaveral.common.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.decathlon.canaveral.common.model.Player
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CameraUtils(private val activity: Activity?) {

    fun getBitmapRotationCleared(tempUri: Uri, bitmap: Bitmap) :Bitmap? {
        activity?.contentResolver?.let {
            tempUri.path?.let { it1 ->
                val ei = ExifInterface(it1)
                val orientation: Int = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

                return when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270)
                    ExifInterface.ORIENTATION_NORMAL -> bitmap
                    else -> rotateImage(bitmap, 270)
                }
            }
        }
        return bitmap
    }

    fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(activity?.contentResolver, inImage, null, null)
        return Uri.parse(path)
    }

    fun getImageFile(imageTitle: String): File {
        val cw = ContextWrapper(activity as Context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val file = File(directory, imageTitle)
        if (file.exists()) file.delete()
        return file
    }

    fun saveImage(context: Context, bitmap: Bitmap, imageTitle: String) :Uri {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val path = File(directory, imageTitle)
        var fos: FileOutputStream? = null

        try {
            fos = FileOutputStream(path)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return Uri.fromFile(path)
    }

    fun handleSamplingAndRotationBitmap(selectedImage: Uri): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var imageStream = (activity as ContextWrapper).contentResolver?.openInputStream(selectedImage)
        BitmapFactory.decodeStream(imageStream, null, options)
        imageStream?.close()

        options.inSampleSize = calculateInSampleSize(options)

        options.inJustDecodeBounds = false
        imageStream = (activity as ContextWrapper).contentResolver?.openInputStream(selectedImage)
        var img: Bitmap? = BitmapFactory.decodeStream(imageStream, null, options)
        img = img?.let { rotateImageIfRequired(it, selectedImage) }
        return img
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options
    ): Int {

        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > MAX_HEIGHT || width > MAX_WIDTH) {
            val heightRatio = (height.toFloat() / MAX_HEIGHT.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / MAX_WIDTH.toFloat()).roundToInt()

            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            val totalPixels = (width * height).toFloat()

            val totalReqPixelsCap = (MAX_WIDTH * MAX_HEIGHT * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }
        }
        return inSampleSize
    }

    private fun rotateImageIfRequired(img: Bitmap, selectedImage: Uri): Bitmap? {
        val ei = ExifInterface(
            selectedImage.path!!
        )
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    companion object {
        const val MAX_HEIGHT = 1024
        const val MAX_WIDTH = 1024

        fun getPlayerImageName(player :Player) :String {
            val dateTimeString = SimpleDateFormat("yyyyMMdd-HHmmss",
                Locale.getDefault()).format(Date())
            return "player"+player.id.toString()+"_$dateTimeString.jpeg"
        }
    }

}