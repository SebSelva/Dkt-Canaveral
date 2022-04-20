package com.decathlon.canaveral.game.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseDialogFragment
import com.decathlon.canaveral.databinding.FragmentTransitionInfoBinding
import kotlinx.coroutines.delay

class GameTransitionInfoFragment: BaseDialogFragment<FragmentTransitionInfoBinding>() {

    override var layoutId = R.layout.fragment_transition_info
    private val args: GameTransitionInfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        _binding.playerRoundScore.text = args.info

        backToGame()
    }

    private fun backToGame() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            delay(2000)
            findNavController().popBackStack()
        }
    }
}