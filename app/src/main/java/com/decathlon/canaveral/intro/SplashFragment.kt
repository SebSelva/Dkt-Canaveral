package com.decathlon.canaveral.intro

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.BuildConfig
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.databinding.FragmentSplashScreenBinding
import kotlinx.coroutines.delay
import org.koin.androidx.navigation.koinNavGraphViewModel

class SplashFragment : BaseFragment<FragmentSplashScreenBinding>() {

    override var layoutId = R.layout.fragment_splash_screen
    private val introViewModel by koinNavGraphViewModel<IntroViewModel>(R.id.nav_intro)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.hdcVersion.text = resources.getString(R.string.app_version, BuildConfig.VERSION_NAME)

        lifecycleScope.launchWhenResumed {
            delay(1000)
            if (introViewModel.isLogin()) {
                findNavController().navigate(
                    R.id.action_splash_to_dashboard
                )
            } else {
                findNavController().navigate(
                    R.id.action_splash_to_welcome
                )
            }
        }
    }
}