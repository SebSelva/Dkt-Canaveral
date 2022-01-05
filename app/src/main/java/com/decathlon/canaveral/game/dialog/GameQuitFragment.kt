package com.decathlon.canaveral.game.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseDialogFragment
import com.decathlon.canaveral.databinding.DialogfragmentGameQuitBinding

class GameQuitFragment: BaseDialogFragment<DialogfragmentGameQuitBinding>() {

    override var layoutId = R.layout.dialogfragment_game_quit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        _binding.no.setOnClickListener { dismiss() }
        _binding.yes.setOnClickListener { activity?.finish() }
    }
}