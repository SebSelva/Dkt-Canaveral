package com.decathlon.canaveral.camera

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.Surface
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.camera.core.*
import androidx.camera.core.ImageCapture.OutputFileOptions
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.navArgs
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseActivity
import com.decathlon.canaveral.common.utils.CameraUtils
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : BaseActivity() {

    private val args: CameraActivityArgs by navArgs()
    override fun getLayoutId(): Int = R.layout.activity_camera

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture

    private lateinit var previewView: PreviewView
    private lateinit var takePicture: AppCompatImageButton

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
        /*cameraHelper = CameraHelper(
            this,
            this,
            _binding.previewView,
            onResult = {
                if (it != null) {
                    val cameraUtils = CameraUtils(this)
                    val uri = cameraUtils.saveImage(this, it, args.filename)
                    cameraUtils.handleSamplingAndRotationBitmap(uri)
                    val intent = Intent()
                    intent.putExtra(PICTURE_FILENAME, uri.toString())
                    setResult(RESULT_OK, intent)
                }
                cameraHelper.stop()
                finish()
            }
        )
        cameraHelper.start()*/

        takePicture.setOnClickListener {
            captureImage {
                if (it != null) {
                    /*val cameraUtils = CameraUtils(this)
                    val uri = cameraUtils.saveImage(this, it, args.filename)
                    cameraUtils.handleSamplingAndRotationBitmap(uri)*/
                    val intent = Intent()
                    intent.putExtra(PICTURE_FILENAME, it)
                    setResult(RESULT_OK, intent)
                    finish()
                }
                else Timber.e("Captured image is null")
            }
            //cameraHelper.captureImage()
        }
    }

    override fun onStop() {
        super.onStop()
        //cameraExecutor.shutdown()
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

        val camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview, imageCapture)
        observeCameraState(camera.cameraInfo)
    }

    private fun observeCameraState(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(this as LifecycleOwner) { cameraState ->
            run {
                val msg = when (cameraState.type) {
                    CameraState.Type.PENDING_OPEN -> {
                        // Ask the user to close other camera apps
                        "CameraState: Pending Open"
                    }
                    CameraState.Type.OPENING -> {
                        // Show the Camera UI
                        "CameraState: Opening"
                    }
                    CameraState.Type.OPEN -> {
                        // Setup Camera resources and begin processing
                        "CameraState: Open"
                    }
                    CameraState.Type.CLOSING -> {
                        // Close camera UI
                        "CameraState: Closing"
                    }
                    CameraState.Type.CLOSED -> {
                        // Free camera resources
                        "CameraState: Closed"
                    }
                }
                msg.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            }

            cameraState.error?.let { error ->
                val msg = when (error.code) {
                    // Open errors
                    CameraState.ERROR_STREAM_CONFIG -> {
                        // Make sure to setup the use cases properly
                        "Stream config error"
                    }
                    // Opening errors
                    CameraState.ERROR_CAMERA_IN_USE -> {
                        // Close the camera or ask user to close another camera app that's using the
                        // camera
                        "Camera in use"
                    }
                    CameraState.ERROR_MAX_CAMERAS_IN_USE -> {
                        // Close another open camera in the app, or ask the user to close another
                        // camera app that's using the camera
                        "Max cameras in use"
                    }
                    CameraState.ERROR_OTHER_RECOVERABLE_ERROR -> {
                        "Other recoverable error"
                    }
                    // Closing errors
                    CameraState.ERROR_CAMERA_DISABLED -> {
                        // Ask the user to enable the device's cameras
                        "Camera disabled"
                    }
                    CameraState.ERROR_CAMERA_FATAL_ERROR -> {
                        // Ask the user to reboot the device to restore camera function
                        "Fatal error"
                    }
                    // Closed errors
                    CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED -> {
                        // Ask the user to disable the "Do Not Disturb" mode, then reopen the camera
                        "Do not disturb mode enabled"
                    }
                    else -> { null }
                }
                msg?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            }
        }
    }


    private fun captureImage(onResult: (result: String?) -> Unit) {
        val dateTimeString = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        val outputFileOptions = OutputFileOptions.Builder(CameraUtils(this).getImageFile(args.filename +"_" +dateTimeString)).build()
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                onResult(outputFileResults.savedUri?.toString())
            }
            override fun onError(exc: ImageCaptureException) {
                Timber.e(exc)
                onResult(null)
            }
        });
        /*imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onCaptureSuccess(image: ImageProxy) {
                val buffer = image.planes.first().buffer
                val bytes = ByteArray(buffer.capacity())
                buffer.get(bytes)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
                image.close()
                onResult.invoke(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                onResult.invoke(null)
            }
        })*/
    }

    companion object {
        const val PICTURE_FILENAME = "CameraBitmapFileName"
    }
}