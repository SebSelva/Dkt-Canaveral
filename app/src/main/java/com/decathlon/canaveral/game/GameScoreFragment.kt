package com.decathlon.canaveral.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.decathlon.canaveral.R
import com.decathlon.canaveral.databinding.FragmentScoreBinding
import com.decathlon.canaveral.view.ScoreTextView
import kotlinx.coroutines.delay

class GameScoreFragment: DialogFragment() {

    val args: GameScoreFragmentArgs by navArgs()
    private lateinit var _binding: FragmentScoreBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_score, container, false)
        _binding = FragmentScoreBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)

        _binding.playerRoundScore.text = args.roundScore.toString()

        backToGame()
    }

    private fun backToGame() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            delay(2000)
            findNavController().popBackStack()
        }
    }
}