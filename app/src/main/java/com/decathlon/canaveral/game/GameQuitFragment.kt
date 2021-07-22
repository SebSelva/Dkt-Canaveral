package com.decathlon.canaveral.game

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.databinding.DialogfragmentGameQuitBinding
import com.decathlon.canaveral.databinding.FragmentGameOptionsBinding

class GameQuitFragment: DialogFragment() {

    private lateinit var _binding: DialogfragmentGameQuitBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialogfragment_game_quit, container, false)
        _binding = DialogfragmentGameQuitBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        _binding.no.setOnClickListener { dismiss() }
        _binding.yes.setOnClickListener { activity?.finish() }
    }
}