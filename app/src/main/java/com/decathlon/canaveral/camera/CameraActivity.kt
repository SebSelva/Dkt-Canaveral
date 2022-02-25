package com.decathlon.canaveral.camera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.navArgs
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseActivity
import com.decathlon.canaveral.common.utils.CameraUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber

class CameraActivity : BaseActivity() {

    private val args: CameraActivityArgs by navArgs()
    override fun getLayoutId(): Int = R.layout.activity_camera

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture

    private lateinit var previewView: PreviewView
    private lateinit var takePicture: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        previewView = findViewById(R.id.previewView)
        takePicture = findViewById(R.id.takePicture)

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()

        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation : Int) {
                val rotation : Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }
                imageCapture.targetRotation = rotation
            }
        }
        orientationEventListener.enable()

        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        takePicture.setOnClickListener {
            captureImage {
                if (it != null) {
                    val intent = Intent()
                    intent.putExtra(PICTURE_FILENAME, it)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                else Timber.e("Captured image is null")
            }
        }
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        cameraProvider.unbindAll()

        previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        previewView.scaleType = PreviewView.ScaleType.FILL_CENTER

        val preview : Preview = Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(previewView.surfaceProvider) }

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageCapture)
    }


    private fun captureImage(onResult: (result: String?) -> Unit) {
        val outputFileOptions = OutputFileOptions.Builder(CameraUtils.getImageFile(this, args.filename)).build()
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onResult(outputFileResults.savedUri?.toString())
            }
            override fun onError(exc: ImageCaptureException) {
                Timber.e(exc)
                onResult(null)
            }
        })
    }

    companion object {
        const val PICTURE_FILENAME = "CameraBitmapFileName"
    }
}