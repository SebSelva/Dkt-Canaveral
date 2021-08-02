package com.decathlon.canaveral.game.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.databinding.FragmentGameOptionsBinding

class GameOptionsFragment: DialogFragment() {

    private lateinit var _binding: FragmentGameOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_options, container, false)
        _binding = FragmentGameOptionsBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        _binding.optionsResume.setOnClickListener { findNavController().popBackStack() }
        _binding.optionsQuit.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.action_options_to_quit) }
    }
}