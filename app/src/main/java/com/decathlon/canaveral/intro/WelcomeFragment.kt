package com.decathlon.canaveral.intro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.databinding.FragmentWelcomeBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>() {
    override var layoutId = R.layout.fragment_welcome
    private val loginViewModel by sharedViewModel<LoginViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.welcomeGuest.setOnClickListener {
            findNavController().navigate(R.id.action_welcome_to_dashboard)
        }
        _binding.welcomeLogin.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                if (getNetworkStatus()) {
                    loginViewModel.showFirstTimeConsent(requireActivity())
                    loginViewModel.requestLogIn()
                }
            }
        }

        loginViewModel.uiState().observe(viewLifecycleOwner) { logInUiState ->
            when (logInUiState) {
                is LoginViewModel.LoginUiState.LoginFailed -> {
                    Timber.w("Authentication login failed")
                }
                is LoginViewModel.LoginUiState.LoginInProgress -> {
                    Timber.d("Authentication login in progress")
                    //Toast.makeText(requireContext(), "User login in progress", Toast.LENGTH_LONG).show()
                }
                is LoginViewModel.LoginUiState.UserInfoSuccess -> {
                    Timber.d("Login & UserInfo success")
                    findNavController().navigate(
                        R.id.action_welcome_to_login_success)
                }
                is LoginViewModel.LoginUiState.UserInfoFailed -> {
                    Timber.d("UserInfo failed")
                    Toast.makeText(requireContext(), "Get user info failed", Toast.LENGTH_LONG).show()
                }
            }
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
}