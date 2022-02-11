package com.decathlon.canaveral.player

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.decathlon.canaveral.R
import com.decathlon.canaveral.camera.CameraActivity
import com.decathlon.canaveral.camera.CameraActivityArgs
import com.decathlon.canaveral.common.BaseDialogFragment
import com.decathlon.canaveral.common.utils.CameraUtils
import com.decathlon.canaveral.databinding.DialogPlayerEditionBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerEditionFragment: BaseDialogFragment<DialogPlayerEditionBinding>() {
    override var layoutId = R.layout.dialog_player_edition
    private val args: PlayerEditionFragmentArgs by navArgs()

    private val playerEditionViewModel by viewModel<PlayerEditionViewModel>()

    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { setPlayerPictureUri(it.data?.toString()) }
        }
    }

    private val requestCameraPermissionResult = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) { startCamera() }
        }

    private val cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uriReceived = result.data?.extras?.get(CameraActivity.PICTURE_FILENAME) as String?
            setPlayerPictureUri(uriReceived)
        }
    }

    private fun setPlayerPictureUri(uriReceived: String?) {
        playerEditionViewModel.player.image = uriReceived
        _binding.player = playerEditionViewModel.player
        _binding.notifyChange()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        playerEditionViewModel.player = args.player
        _binding.player = playerEditionViewModel.player
        _binding.executePendingBindings()

        _binding.optionCamera.setOnClickListener {
            val pictureMenu = PopupMenu(requireContext(), _binding.optionCamera)
            pictureMenu.menuInflater.inflate(R.menu.option_picture, pictureMenu.menu)
            pictureMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.camera -> {
                        val permissionGranted = requestCameraPermission()
                        if (permissionGranted) {
                            startCamera()
                        }
                    }
                    R.id.gallery -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        galleryResult.launch(intent)
                    }
                }
                true
            }
            pictureMenu.show()
        }

        _binding.editionValidate.setOnClickListener {
            if (!_binding.inputPlayerName.text.isNullOrEmpty()) {
                playerEditionViewModel.player.nickname = _binding.inputPlayerName.text.toString()
            }
            playerEditionViewModel.updatePlayer()
            findNavController().popBackStack()
        }

        _binding.editionClose.setOnClickListener {
            dismiss()
        }

        _binding.editionCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun startCamera() {
        val tempImage = CameraUtils.getPlayerImageName(args.player)
        val intent = Intent(requireContext(), CameraActivity::class.java)
        val bundle = CameraActivityArgs(tempImage).toBundle()
        intent.putExtras(bundle)
        cameraResult.launch(intent)
    }

    private fun requestCameraPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermissionResult.launch(Manifest.permission.CAMERA)
            return false
        }
        return true
    }
}