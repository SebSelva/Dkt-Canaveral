package com.decathlon.canaveral.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.model.X01Player
import com.decathlon.canaveral.databinding.FragmentGameEndBinding
import com.decathlon.canaveral.game.adapter.PlayersStatsAdapter

class GameEndStatsFragment : Fragment() {

    private val args: GameEndStatsFragmentArgs by navArgs()
    private lateinit var _binding: FragmentGameEndBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game_end, container, false)
        _binding = FragmentGameEndBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sortedPlayers = args.x01PlayerList
        sortedPlayers.sortBy { x01Player -> x01Player.remainingPoints }
        val winPlayers = emptyList<X01Player>().toMutableList()
        var bestScore: Int? = null
        sortedPlayers.forEach {
            if (bestScore == null) bestScore = it.remainingPoints
            if (bestScore == it.remainingPoints) winPlayers.add(it)
        }

        // Title
        _binding.endTitle.text = if (sortedPlayers.size > 1 && winPlayers.size == sortedPlayers.size) {
            resources.getString(R.string.game_end_draw)
        } else {
            val winPlayersIterator = winPlayers.iterator()
            when (winPlayers.size) {
                1 -> resources.getString(
                    R.string.game_end_win_one,
                    winPlayersIterator.next().nickname
                )

                2 -> resources.getString(
                    R.string.game_end_win_two,
                    winPlayersIterator.next().nickname,
                    winPlayersIterator.next().nickname
                )

                3 -> resources.getString(
                    R.string.game_end_win_three,
                    winPlayersIterator.next().nickname,
                    winPlayersIterator.next().nickname,
                    winPlayersIterator.next().nickname
                )
                else -> resources.getString(R.string.game_end_draw)
            }
        }

        // Winning players
        _binding.endWinningPlayers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val winningPlayersStatsAdapter = PlayersStatsAdapter(true)
        _binding.endWinningPlayers.adapter = winningPlayersStatsAdapter
        winningPlayersStatsAdapter.setData(winPlayers)

        // Losing players
        _binding.endLostPlayers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val otherPlayersStatsAdapter = PlayersStatsAdapter(false)
        _binding.endLostPlayers.adapter = otherPlayersStatsAdapter
        otherPlayersStatsAdapter.setData(sortedPlayers.toList().subList(winPlayers.size, sortedPlayers.size))


        // Buttons
        _binding.buttonCheck.setOnClickListener { activity?.finish() }
        _binding.buttonReload.setOnClickListener {
            /* Reload Game */
            Navigation.findNavController(view).navigate(
                R.id.action_end_to_new_game,
                GameActivityArgs(
                    args.variantIndex,
                    args.isBull25,
                    args.nbRound,
                    args.inIndex,
                    args.outIndex
                ).toBundle())
        }
    }

}