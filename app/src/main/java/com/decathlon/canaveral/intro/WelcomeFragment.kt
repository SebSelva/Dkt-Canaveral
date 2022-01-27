package com.decathlon.canaveral.intro

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.common.utils.ContextUtils
import com.decathlon.canaveral.common.utils.showSnackBar
import com.decathlon.canaveral.databinding.FragmentWelcomeBinding
import org.koin.androidx.navigation.koinNavGraphViewModel
import timber.log.Timber

class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>() {
    override var layoutId = R.layout.fragment_welcome
    private val introViewModel by koinNavGraphViewModel<IntroViewModel>(R.id.nav_intro)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.welcomeGuest.setOnClickListener {
            findNavController().navigate(
                R.id.action_welcome_to_dashboard)
        }
        _binding.welcomeLogin.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                if (ContextUtils.isInternetAvailable(requireContext())) {
                    introViewModel.requestLogIn()
                } else {
                    showSnackBar(getString(R.string.common_internet_error))
                }
            }
        }

        introViewModel.uiState().observe(viewLifecycleOwner) { logInUiState ->
            when (logInUiState) {
                is IntroViewModel.LoginUiState.LoginFailed -> {
                    Timber.w("Authentication login error")
                }
                is IntroViewModel.LoginUiState.LoginInProgress -> {
                    Timber.d("Authentication login in progress")
                    Toast.makeText(requireContext(), "User login failed", Toast.LENGTH_LONG).show()
                }
                is IntroViewModel.LoginUiState.UserInfoSuccess -> {
                    Timber.d("Login & UserInfo success")
                    findNavController().navigate(
                        R.id.action_welcome_to_login_success)
                }
                is IntroViewModel.LoginUiState.UserInfoFailed -> {
                    Timber.d("UserInfo failed")
                    Toast.makeText(requireContext(), "Get user info failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        introViewModel.initLoginContext(requireContext())
    }

    override fun onStop() {
        super.onStop()
        introViewModel.releaseLoginContext()
    }
}