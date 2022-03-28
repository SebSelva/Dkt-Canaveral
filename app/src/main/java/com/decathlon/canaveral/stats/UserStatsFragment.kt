package com.decathlon.canaveral.stats

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.lifecycle.lifecycleScope
import app.futured.donut.DonutSection
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.databinding.FragmentUserStatsBinding
import com.decathlon.canaveral.intro.LoginViewModel
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class UserStatsFragment: BaseFragment<FragmentUserStatsBinding>() {
    override var layoutId = R.layout.fragment_user_stats

    private val loginViewModel by sharedViewModel<LoginViewModel>()
    private val statsViewModel by sharedViewModel<StatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initLoginViews()
        initStatViews()
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.initLoginContext(requireContext())
    }

    override fun onStop() {
        super.onStop()
        loginViewModel.releaseLoginContext()
    }

    private fun initLoginViews() {
        _binding.userLoginAction.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                if (getNetworkStatus()) loginViewModel.requestLogIn()
            }
        }

        loginViewModel.uiState().observe(viewLifecycleOwner) { logInUiState ->
            when (logInUiState) {
                is LoginViewModel.LoginUiState.UserInfoSuccess,
                LoginViewModel.LoginUiState.LogoutSuccess-> {
                    lifecycleScope.launchWhenResumed {
                        setProfile()
                        statsViewModel.getStats()
                    }
                }
            }
        }
        setProfile()
    }

    private fun setProfile() {
        lifecycleScope.launchWhenStarted {
            if (loginViewModel.isLogin()) {
                loginViewModel.getMainUser()?.let { user ->
                    _binding.user = user
                    _binding.profileName.text = user.firstname
                    if (user.nickname.isNotEmpty()) {
                        _binding.profileNickname.text = resources.getString(R.string.profile_nickname, user.nickname)
                    }
                    _binding.userPpdValue.text = String.format("%.2f", 0F)
                    _binding.userMprValue.text = String.format("%.2f", 0F)
                    statsViewModel.getStats()
                }

            } else {
                _binding.user = null
                _binding.profileName.text = "Guest"
                _binding.userPpdValue.text = String.format("%.2f", 0F)
                _binding.userMprValue.text = String.format("%.2f", 0F)
            }
        }
    }

    private fun initStatViews() {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 800
        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        anim.interpolator = LinearInterpolator()
        _binding.loadingMessage.startAnimation(anim)

        statsViewModel.statsLiveData.observe(viewLifecycleOwner) {
            setStatData(it)
        }

        lifecycleScope.launchWhenResumed {
            statsViewModel.getStats()
        }
    }

    private fun setStatData(stat: DartsStatEntity?) {
        _binding.stats = stat
        stat?.let { statValues ->
            val victories = if (statValues.gamesPlayed > 0) {
                (statValues.gamesWon.toFloat() * 100 / statValues.gamesPlayed)
            } else 0F
            val draws = if (statValues.gamesPlayed > 0) {
                (statValues.gamesDraw.toFloat() * 100 / statValues.gamesPlayed)
            } else 0F
            val defeats = if (statValues.gamesPlayed > 0) {
                ((statValues.gamesPlayed - statValues.gamesDraw - statValues.gamesWon).toFloat() * 100 / statValues.gamesPlayed)
            } else 0F

            val simpleDateFormat = SimpleDateFormat("dd.mm.yyyy, hh:mm")
            val dateTime = simpleDateFormat.format(Calendar.getInstance().time)
            _binding.statUpdateDate.text = resources.getString(R.string.stats_last_update, dateTime)

            _binding.userPpdValue.text = String.format("%.2f", (statValues.ppdTotalScored.toFloat() / statValues.dartsThrown))
            //_binding.userMprValue.text = String.format("%.2f", (statValues.TotalScored / statValues.dartsThrown).toFloat())

            setDonutData(victories, draws, defeats)
            _binding.victoriesPercent.text = getIntPercentValue(victories)
            _binding.drawsPercent.text = getIntPercentValue(draws)
            _binding.defeatsPercent.text = getIntPercentValue(defeats)

            startScoreAnimation(_binding.victoriesNumber, 0, statValues.gamesWon.toInt())
            startScoreAnimation(_binding.dartsThrownNumber, 0, statValues.dartsThrown.toInt())
            startScoreAnimation(_binding.playedGamesNumber, 0, statValues.gamesPlayed.toInt())
        }
    }

    private fun getIntPercentValue(victories: Float) =
        String.format("%d %%", victories.roundToInt())

    private fun setDonutData(victories: Float, draws: Float, defeats: Float) {

        val sections = ArrayList<DonutSection>()

        val victoriesSection = DonutSection(
            name = resources.getString(R.string.stats_victories),
            color = resources.getColor(R.color.blue_dkt_400, null),
            amount = victories
        )

        val drawsSection = DonutSection(
            name = resources.getString(R.string.stats_draws),
            color = resources.getColor(R.color.blue_dkt_100, null),
            amount = draws
        )
        val defeatsSection = DonutSection(
            name = resources.getString(R.string.stats_defeats),
            color = resources.getColor(R.color.blue_dkt_200, null),
            amount = defeats
        )
        if (victories > 0) sections.add(victoriesSection)
        if (draws > 0) sections.add(drawsSection)
        if (defeats > 0) sections.add(defeatsSection)

        _binding.userGameStatPieChart.submitData(sections)
    }
}