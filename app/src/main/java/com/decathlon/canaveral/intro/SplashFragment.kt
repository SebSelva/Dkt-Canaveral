package com.decathlon.canaveral.intro

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.common.utils.getAppVersion
import com.decathlon.canaveral.databinding.FragmentSplashScreenBinding
import com.decathlon.canaveral.stats.StatsViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SplashFragment : BaseFragment<FragmentSplashScreenBinding>() {

    override var layoutId = R.layout.fragment_splash_screen
    private val loginViewModel by sharedViewModel<LoginViewModel>()
    private val statsViewModel by sharedViewModel<StatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.hdcVersion.text = getAppVersion(requireContext())

        lifecycleScope.launchWhenResumed {
            if (loginViewModel.isLogin()) {
                statsViewModel.getStats().run {
                    findNavController().navigate(
                        R.id.action_splash_to_dashboard
                    )
                }
            } else {
                delay(1000)
                findNavController().navigate(
                    R.id.action_splash_to_welcome
                )
            }
        }
    }
}