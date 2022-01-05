package com.decathlon.canaveral.game.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseDialogFragment
import com.decathlon.canaveral.databinding.FragmentGameOptionsBinding

class GameOptionsFragment: BaseDialogFragment<FragmentGameOptionsBinding>() {

    override var layoutId = R.layout.fragment_game_options

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        _binding.optionsResume.setOnClickListener { findNavController().popBackStack() }
        _binding.optionsQuit.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.action_options_to_quit) }
    }
}