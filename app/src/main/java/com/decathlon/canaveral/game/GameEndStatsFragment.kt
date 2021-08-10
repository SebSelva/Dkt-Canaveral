package com.decathlon.canaveral.game

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.model.X01PlayerStats
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
        val winPlayers = emptyList<X01PlayerStats>().toMutableList()
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
                    winPlayersIterator.next().player.nickname
                )

                2 -> resources.getString(
                    R.string.game_end_win_two,
                    winPlayersIterator.next().player.nickname,
                    winPlayersIterator.next().player.nickname
                )

                3 -> resources.getString(
                    R.string.game_end_win_three,
                    winPlayersIterator.next().player.nickname,
                    winPlayersIterator.next().player.nickname,
                    winPlayersIterator.next().player.nickname
                )
                else -> resources.getString(R.string.game_end_draw)
            }
        }

        // Winning players
        _binding.endWinningPlayers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val winningPlayersStatsAdapter = PlayersStatsAdapter(true)
        _binding.endWinningPlayers.adapter = winningPlayersStatsAdapter


        // Losing players
        _binding.endLostPlayers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val otherPlayersStatsAdapter = PlayersStatsAdapter(winPlayers.size == sortedPlayers.size)
        _binding.endLostPlayers.adapter = otherPlayersStatsAdapter

        if (sortedPlayers.size > 1 && winPlayers.size == sortedPlayers.size) {
            winningPlayersStatsAdapter.setData(
                winPlayers.subList(0,2),
                resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            otherPlayersStatsAdapter.setData(
                sortedPlayers.toList().subList(2, sortedPlayers.size),
                resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            )
        } else {
            winningPlayersStatsAdapter.setData(
                winPlayers,
                resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            otherPlayersStatsAdapter.setData(
                sortedPlayers.toList().subList(winPlayers.size, sortedPlayers.size),
                resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
            )
        }



        // Buttons
        _binding.buttonCheck.setOnClickListener { activity?.finish() }
        _binding.buttonReload.setOnClickListener {
            Navigation.findNavController(view).navigate(
                R.id.action_end_to_new_game,
                GameActivityArgs(
                    args.variantIndex,
                    args.isBull25,
                    args.roundIndex,
                    args.inIndex,
                    args.outIndex
                ).toBundle())
        }
    }

}