package com.decathlon.canaveral.intro

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.databinding.FragmentAccountConnectedBinding
import kotlinx.coroutines.delay

class LoginSuccessFragment : BaseFragment<FragmentAccountConnectedBinding>() {
    override var layoutId = R.layout.fragment_account_connected

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenResumed {
            delay(TRANSITION_DELAY)
            findNavController().navigate(
                R.id.action_login_ok_to_dashboard)
        }
    }

    companion object {
        const val TRANSITION_DELAY = 1000L
    }
}