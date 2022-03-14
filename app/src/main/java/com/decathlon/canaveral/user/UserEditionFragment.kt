package com.decathlon.canaveral.user

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.camera.CameraActivity
import com.decathlon.canaveral.camera.CameraActivityArgs
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.common.utils.CameraUtils
import com.decathlon.canaveral.databinding.FragmentUserEditionBinding
import com.decathlon.canaveral.intro.LoginViewModel
import com.decathlon.core.user.model.User
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class UserEditionFragment: BaseFragment<FragmentUserEditionBinding>() {
    override var layoutId = R.layout.fragment_user_edition

    private val userEditionViewModel by viewModel<UserEditionViewModel>()
    private val loginViewModel by sharedViewModel<LoginViewModel>()

    private var mainUser: User? = null

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
        setProfile()

        loginViewModel.uiState().observe(viewLifecycleOwner) { logInUiState ->
            when (logInUiState) {
                is LoginViewModel.LoginUiState.UserInfoSuccess -> setProfile()
                is LoginViewModel.LoginUiState.LogoutSuccess -> findNavController().popBackStack()
                else -> Timber.e("Error on login/logout")
            }
        }
    }

    private fun initButtons() {
        _binding.optionCamera.setOnClickListener {
            CameraUtils.getPictureMenu(_binding.optionCamera,
                { if (requestCameraPermission()) startCamera() },
                {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    galleryResult.launch(intent)
                }
            ).show()
        }

        _binding.actionLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                if (getNetworkStatus()) loginViewModel.requestLogout()
            }
        }

        _binding.actionSave.setOnClickListener {
            mainUser!!.nickname = _binding.inputPlayerName.text.toString()
            userEditionViewModel.updateUser(mainUser!!)
            findNavController().popBackStack()
        }

        _binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.initLoginContext(requireContext())
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.releaseLoginContext()
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

    private fun startCamera() {
        val tempImage = CameraUtils.getImageName(mainUser?.uid?:0)
        val intent = Intent(requireContext(), CameraActivity::class.java)
        val bundle = CameraActivityArgs(tempImage).toBundle()
        intent.putExtras(bundle)
        cameraResult.launch(intent)
    }

    private fun setPlayerPictureUri(uriReceived: String?) {
        mainUser!!.image = uriReceived
        _binding.user = mainUser
    }

    private fun setProfile() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mainUser = if (loginViewModel.isLogin()) loginViewModel.getMainUser() else null
            _binding.user = mainUser
            if (mainUser?.firstname?.isNotBlank() == true) {
                _binding.hiUsername.text = resources.getString(R.string.user_hi, mainUser!!.firstname.uppercase())
            }
            _binding.executePendingBindings()
        }
    }
}