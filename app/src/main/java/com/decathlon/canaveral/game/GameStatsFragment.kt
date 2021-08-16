package com.decathlon.canaveral.game

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.common.model.PlayerStats
import com.decathlon.canaveral.databinding.FragmentGameEndBinding
import com.decathlon.canaveral.game.adapter.PlayersStatsAdapter

class GameStatsFragment(private val gameTypeIndex: Int) : BaseFragment<FragmentGameEndBinding>() {

    private val args: GameStatsFragmentArgs by navArgs()
    override var layoutId = R.layout.fragment_game_end

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sortedPlayers = args.playerList
        sortedPlayers.sortByDescending { x01Player -> x01Player.currentScore }
        val winPlayers = emptyList<PlayerStats>().toMutableList()
        var bestScore: Int? = null
        sortedPlayers.forEach {
            if (bestScore == null) bestScore = it.currentScore
            if (bestScore == it.currentScore) winPlayers.add(it)
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
        val winningPlayersStatsAdapter = PlayersStatsAdapter(gameTypeIndex,true)
        _binding.endWinningPlayers.adapter = winningPlayersStatsAdapter

        // Losing players
        _binding.endLostPlayers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val otherPlayersStatsAdapter = PlayersStatsAdapter(gameTypeIndex,winPlayers.size == sortedPlayers.size)
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
                    0,
                    args.variantIndex,
                    args.isBull25,
                    args.roundIndex,
                    args.inIndex,
                    args.outIndex
                ).toBundle())
        }
    }

}