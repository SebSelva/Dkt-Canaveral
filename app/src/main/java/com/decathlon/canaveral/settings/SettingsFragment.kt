package com.decathlon.canaveral.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.common.utils.getLanguageNameFromCode
import com.decathlon.canaveral.common.utils.isInternetAvailable
import com.decathlon.canaveral.common.utils.showSnackBar
import com.decathlon.canaveral.databinding.FragmentSettingsBinding
import com.decathlon.canaveral.intro.LoginViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class SettingsFragment: BaseFragment<FragmentSettingsBinding>() {

    override var layoutId: Int = R.layout.fragment_settings
    private val loginViewModel by viewModel<LoginViewModel>()
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // PROFILE
        setProfile()
        _binding.profileConnectAction.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                if (isInternetAvailable(requireContext())) {
                    loginViewModel.requestLogIn()
                } else {
                    showSnackBar(getString(R.string.common_internet_error))
                }
            }
        }
        _binding.profileDisconnectAction.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                if (isInternetAvailable(requireContext())) {
                    loginViewModel.requestLogout()
                } else {
                    showSnackBar(getString(R.string.common_internet_error))
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
                }
                is LoginViewModel.LoginUiState.UserInfoSuccess -> {
                    Timber.d("Login & UserInfo success")
                    setProfile()
                }
                is LoginViewModel.LoginUiState.UserInfoFailed -> {
                    Timber.d("UserInfo failed")
                    Toast.makeText(requireContext(), "Get user info failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        // LANG
        val languageCodes = resources.getStringArray(R.array.languages_code)
        var langCode = localeUtils.getCurrentLocale()!!.language

        val languagesAdapter = SettingsItemListAdapter {
            langCode = languageCodes[it]
            _binding.settingsLanguage.text = getLanguageNameFromCode(view.context, langCode)
            toggleItemList(_binding.languageRecyclerview, false)
            localeUtils.setCurrentLocale(view.context, Locale(langCode))
            refreshCurrentFragment()
        }
        _binding.languageRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        _binding.languageRecyclerview.adapter = languagesAdapter
        languagesAdapter.setData(languageCodes.asList(), langCode)
        toggleItemList(_binding.languageRecyclerview, true) // Hide list at startup

        _binding.settingsLanguage.text = getLanguageNameFromCode(view.context, langCode)
        _binding.settingsLanguage.setOnClickListener {
            // Open recycler view
            val langIndex = languageCodes.indexOf(langCode)
            (_binding.languageRecyclerview.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(if (langIndex > 1) langIndex-1 else 0, 0)
            toggleItemList(_binding.languageRecyclerview, false)
        }
    }

    private fun setProfile() {
        lifecycleScope.launchWhenStarted {
            if (loginViewModel.isLogin() && loginViewModel.getMainUser() != null) {
                _binding.user = loginViewModel.getMainUser()
            } else {
                _binding.profileName.text = "HdcGuest"
            }
        }
    }

    private fun toggleItemList(view: View, forceClose: Boolean) {
        val animationDuration = resources.getInteger(R.integer.animation_duration).toLong()

        val isOpened = view.isVisible
        if (isOpened || forceClose) {
            view.animate().apply {
                duration = animationDuration
                translationY(-view.height.toFloat())
                alpha(0F)
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.isVisible = false
                    }
                })
            }
        } else {
            view.isVisible = true
            view.animate().apply {
                duration = animationDuration
                translationY(0F)
                alpha(1F)
                setListener(null)
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