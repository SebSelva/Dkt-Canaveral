package com.decathlon.canaveral.loading

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.databinding.FragmentLoadingGameBinding
import com.decathlon.canaveral.game.GameActivityArgs
import com.decathlon.canaveral.game.countup.GameCountUpFragmentArgs
import com.decathlon.canaveral.game.x01.Game01FragmentArgs
import kotlinx.coroutines.delay

class LoadingGameFragment: BaseFragment<FragmentLoadingGameBinding>() {

    private lateinit var args: GameActivityArgs
    override var layoutId = R.layout.fragment_loading_game

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity?.intent?.extras != null) {
            args = GameActivityArgs.fromBundle(activity?.intent?.extras!!)
        }

        lifecycleScope.launchWhenResumed {
            delay(600)
            _binding.loadingProgress.progress = 30
            delay(400)
            _binding.loadingProgress.progress = 75
            delay(600)
            _binding.loadingProgress.progress = 100
            delay(200)

            when(resources.getStringArray(R.array.game_type_array)[args.gameTypeIndex]) {
                resources.getString(R.string.game_type_01_game) -> {
                    Navigation.findNavController(view).navigate(
                        R.id.action_loading_to_01game,
                        Game01FragmentArgs(
                            args.gameTypeIndex,
                            args.variantIndex,
                            args.isBull25,
                            args.roundIndex,
                            args.inIndex,
                            args.outIndex
                        ).toBundle())
                }
                resources.getString(R.string.game_type_count_up) -> {
                    Navigation.findNavController(view).navigate(
                        R.id.action_loading_to_countup,
                        GameCountUpFragmentArgs(
                            args.gameTypeIndex,
                            args.variantIndex,
                            args.isBull25,
                            args.roundIndex
                        ).toBundle())
                }
                else -> activity?.finish()
            }
        }
    }
}