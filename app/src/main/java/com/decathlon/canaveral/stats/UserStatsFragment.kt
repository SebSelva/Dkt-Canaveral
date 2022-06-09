package com.decathlon.canaveral.stats

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.futured.donut.DonutSection
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.common.utils.DartsUtils
import com.decathlon.canaveral.common.utils.DartsUtils.Companion.getIntPercentValue
import com.decathlon.canaveral.common.utils.DartsUtils.Companion.getRate
import com.decathlon.canaveral.databinding.FragmentUserStatsBinding
import com.decathlon.canaveral.intro.LoginViewModel
import com.decathlon.canaveral.stats.adapter.FieldsStatsAdapter
import com.decathlon.canaveral.stats.adapter.GameStatsAdapter
import com.decathlon.canaveral.stats.adapter.TricksStatsAdapter
import com.decathlon.canaveral.stats.model.GameStats
import com.decathlon.canaveral.stats.model.StatItem
import com.decathlon.core.gamestats.data.source.room.entity.DartsStatEntity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*


class UserStatsFragment: BaseFragment<FragmentUserStatsBinding>() {
    override var layoutId = R.layout.fragment_user_stats

    private val loginViewModel by sharedViewModel<LoginViewModel>()
    private val statsViewModel by sharedViewModel<StatsViewModel>()

    private var userStats: DartsStatEntity? = null

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
                        statsViewModel.updateStats()
                    }
                }
            }
        }
        setProfile()
    }

    private fun setProfile() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.getMainUser().collect { user ->
                _binding.user = user
                _binding.profileName.text = user?.firstname ?: "Guest"
                if (user?.nickname?.isNotEmpty() == true) {
                    _binding.profileNickname.text = resources.getString(R.string.profile_nickname, user.nickname)
                }
                statsViewModel.getStats()
                statsViewModel.updateStats()
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

        statsViewModel.uiState().observe(viewLifecycleOwner) {
            when (it) {
                is StatsViewModel.StatsViewState.StatsNetworkError -> {
                    // TODO remove
                    Toast.makeText(requireContext(), getString(R.string.common_internet_error) +" " +it.errorCode, Toast.LENGTH_LONG).show()
                }
                is StatsViewModel.StatsViewState.StatsComplete -> {
                    // TODO remove
                    Toast.makeText(requireContext(), "Statistics updated", Toast.LENGTH_LONG).show()
                }
            }
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

            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy, HH:mm", Locale.getDefault())
            val dateTime = simpleDateFormat.format(Date(statValues.dateTime))
            _binding.statUpdateDate.text = resources.getString(R.string.stats_last_update, dateTime)

            _binding.userPpdValue.text = String.format("%.2f",
                if (statValues.ppdDartsThrown > 0) (statValues.ppdTotalScored.toFloat() / statValues.ppdDartsThrown) else 0F)
            _binding.userMprValue.text = String.format("%.2f",
                if (statValues.dartsThrown > 0) (statValues.mpr.toFloat() / statValues.dartsThrown) else 0F)

            setDonutData(victories, draws, defeats)
            _binding.victoriesPercent.text = getIntPercentValue(victories)
            _binding.drawsPercent.text = getIntPercentValue(draws)
            _binding.defeatsPercent.text = getIntPercentValue(defeats)

            startScoreAnimation(_binding.victoriesNumber,
                if (userStats != null) userStats!!.gamesWon.toInt() else 0,
                statValues.gamesWon.toInt())
            startScoreAnimation(_binding.dartsThrownNumber,
                if (userStats != null) userStats!!.dartsThrown.toInt() else 0,
                statValues.dartsThrown.toInt())
            startScoreAnimation(_binding.playedGamesNumber,
                if (userStats != null) userStats!!.gamesPlayed.toInt() else 0,
                statValues.gamesPlayed.toInt())

            setStatsByGame(statValues)
            setTricksStats(statValues)
            setFieldsStats(statValues)
        }
        userStats = stat
    }

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

    private fun setStatsByGame(stat: DartsStatEntity) {
        val statGameList = ArrayList<GameStats>()

        statGameList.add(getGame01Stats(stat))
        statGameList.add(getGameCountUpStats(stat))
        //statGameList.add(getGameCricketStats(stat))

        val gameStatsAdapter = GameStatsAdapter()
        _binding.gameStatsRecyclerView.layoutManager = LinearLayoutManager(
            _binding.gameStatsRecyclerView.context,
            LinearLayoutManager.VERTICAL, false)
        _binding.gameStatsRecyclerView.adapter = gameStatsAdapter
        gameStatsAdapter.setData(statGameList)
    }

    private fun getGame01Stats(stat: DartsStatEntity): GameStats {
        val game01PlayedGames = StatItem(R.string.stats_game_played_games, stat.game01)
        val game01Ppd =
            StatItem(R.string.stats_game_ppd,
                if (stat.ppdDartsThrown > 0) stat.ppdTotalScored.toFloat() / stat.ppdDartsThrown
                else 0F
            )
        val game01WinRate =
            StatItem(R.string.stats_game_winning_percentage, getIntPercentValue(getRate(stat.game01Won, stat.game01)))

        return GameStats(R.string.game_type_01_game,
            listOf(game01PlayedGames, game01Ppd, game01WinRate)
        )
    }

    private fun getGameCountUpStats(stat: DartsStatEntity): GameStats {
        val countupGamesPlayed = StatItem(R.string.stats_game_played_games, stat.gameCountup)
        val countupHighestScore =
            StatItem(R.string.stats_game_highest_score, stat.highScoreOn8Rounds.toFloat())
        val countupWinRate =
            StatItem(R.string.stats_game_winning_percentage, getIntPercentValue(getRate(stat.gameCountupWon, stat.gameCountup)))

        return GameStats(R.string.game_type_count_up,
            listOf(countupGamesPlayed, countupHighestScore, countupWinRate)
        )
    }

    /* Cricket game not available yet
    private fun getGameCricketStats(stat: DartsStatEntity): GameStats {
        val cricketGamesPlayed = StatItem(R.string.stats_game_played_games, stat.gameCricket)
        val cricketMpr = StatItem(R.string.stats_game_mpr, stat.mpr.toFloat())
        val cricketWinRate = StatItem(
            R.string.stats_game_winning_percentage, getIntPercentValue(getRate(stat.gameCricketWon, stat.gameCricket))
        )

        return GameStats(R.string.game_type_cricket,
            listOf(cricketGamesPlayed, cricketMpr, cricketWinRate)
        )
    }*/

    private fun setTricksStats(stat: DartsStatEntity) {
        val babyTon = StatItem(R.string.stats_trick_baby_ton, stat.babyTonTrick)
        val bagoNuts = StatItem(R.string.stats_trick_bag_o_nuts, stat.bagONutsTrick)
        val bullsEye = StatItem(R.string.stats_trick_bulls_eye, stat.bullEyesTrick)
        val bust = StatItem(R.string.stats_trick_bust, stat.bustTrick)
        val hatTrick = StatItem(R.string.stats_trick_hattrick, stat.hatTrick)
        val highTon = StatItem(R.string.stats_trick_high_ton, stat.highTownTrick)
        val lowTon = StatItem(R.string.stats_trick_low_ton, stat.lowTownTrick)
        val threeInaBed = StatItem(R.string.stats_trick_three_in_a_bed, stat.threeInABedTrick)
        val ton = StatItem(R.string.stats_trick_ton, stat.tonTrick)
        val ton80 = StatItem(R.string.stats_trick_ton_80, stat.ton80Trick)
        val whiteHorse = StatItem(R.string.stats_trick_white_horse, stat.whiteHorseTrick)
        val roundMore60 = StatItem(R.string.stats_trick_round_score_60_and_more, stat.roundMore60Trick)
        val roundMore100 = StatItem(R.string.stats_trick_round_score_100_and_more, stat.roundMore100Trick)
        val roundMore140 = StatItem(R.string.stats_trick_round_score_140_and_more, stat.roundMore140Trick)
        val round180 = StatItem(R.string.stats_trick_round_score_180, stat.round180Trick)
        val field15 = StatItem(R.string.stats_trick_field_15, stat.dart15Count)
        val field16 = StatItem(R.string.stats_trick_field_16, stat.dart16Count)
        val field17 = StatItem(R.string.stats_trick_field_17, stat.dart17Count)
        val field18 = StatItem(R.string.stats_trick_field_18, stat.dart18Count)
        val field19 = StatItem(R.string.stats_trick_field_19, stat.dart19Count)
        val field20 = StatItem(R.string.stats_trick_field_20, stat.dart20Count)
        val fieldBull = StatItem(R.string.stats_trick_field_bull, stat.dartBullCount)
        val highestRound = StatItem(R.string.stats_trick_highest_score_round, stat.roundHighest)
        val avgScoreDart1 = StatItem(R.string.stats_trick_avg_score_dart1, stat.averageScoreDart1)
        val avgScoreDart2 = StatItem(R.string.stats_trick_avg_score_dart2, stat.averageScoreDart2)
        val avgScoreDart3 = StatItem(R.string.stats_trick_avg_score_dart3, stat.averageScoreDart3)
        val checkoutRate = StatItem(R.string.stats_trick_checkout_rate, stat.checkoutPercent)
        val highestCheckout = StatItem(R.string.stats_trick_highest_checkout, stat.checkoutRecordH)
        val highScore8Rounds = StatItem(R.string.stats_trick_highest_8rounds, stat.highScoreOn8Rounds)
        val highScore12Rounds = StatItem(R.string.stats_trick_highest_12rounds, stat.highScoreOn12Rounds)
        val highScore16Rounds = StatItem(R.string.stats_trick_highest_16rounds, stat.highScoreOn16Rounds)
        val avgRoundScore = StatItem(R.string.stats_trick_avg_score_round, stat.averageScoreRound)

        val trickList = mutableListOf(babyTon, bagoNuts, bullsEye, bust, hatTrick, highTon, lowTon,
            threeInaBed, ton, ton80, whiteHorse, roundMore60, roundMore100, roundMore140, round180,
            field15, field16, field17, field18, field19, field20, fieldBull, highestRound,
            avgScoreDart1, avgScoreDart2, avgScoreDart3, checkoutRate, highestCheckout,
            highScore8Rounds, highScore12Rounds, highScore16Rounds, avgRoundScore)

        val spanCount = if (Configuration.ORIENTATION_LANDSCAPE == resources.configuration.orientation) 6 else 3
        val tricksAdapter = TricksStatsAdapter(requireContext()) { statItem ->
            findNavController().navigate(
                R.id.action_user_stats_to_trick_info,
                TrickInfoDialogArgs(statItem.title, DartsUtils.getIntValue(statItem.value)).toBundle()
            )
        }
        _binding.gameTricksRecyclerView.layoutManager = GridLayoutManager(
            requireContext(), spanCount, GridLayoutManager.VERTICAL, false)
        _binding.gameTricksRecyclerView.adapter = tricksAdapter
        tricksAdapter.submitList(trickList)
    }

    private fun setFieldsStats(stat: DartsStatEntity) {
        val rateMiss = StatItem(R.string.stats_field_rate_miss, stat.dartMissPercent)
        val rate1 = StatItem(R.string.stats_field_rate_1, stat.dart1Percent)
        val rate2 = StatItem(R.string.stats_field_rate_2, stat.dart2Percent)
        val rate3 = StatItem(R.string.stats_field_rate_3, stat.dart3Percent)
        val rate4 = StatItem(R.string.stats_field_rate_4, stat.dart4Percent)
        val rate5 = StatItem(R.string.stats_field_rate_5, stat.dart5Percent)
        val rate6 = StatItem(R.string.stats_field_rate_6, stat.dart6Percent)
        val rate7 = StatItem(R.string.stats_field_rate_7, stat.dart7Percent)
        val rate8 = StatItem(R.string.stats_field_rate_8, stat.dart8Percent)
        val rate9 = StatItem(R.string.stats_field_rate_9, stat.dart9Percent)
        val rate10 = StatItem(R.string.stats_field_rate_10, stat.dart10Percent)
        val rate11 = StatItem(R.string.stats_field_rate_11, stat.dart11Percent)
        val rate12 = StatItem(R.string.stats_field_rate_12, stat.dart12Percent)
        val rate13 = StatItem(R.string.stats_field_rate_13, stat.dart13Percent)
        val rate14 = StatItem(R.string.stats_field_rate_14, stat.dart14Percent)
        val rate15 = StatItem(R.string.stats_field_rate_15, stat.dart15Percent)
        val rate16 = StatItem(R.string.stats_field_rate_16, stat.dart16Percent)
        val rate17 = StatItem(R.string.stats_field_rate_17, stat.dart17Percent)
        val rate18 = StatItem(R.string.stats_field_rate_18, stat.dart18Percent)
        val rate19 = StatItem(R.string.stats_field_rate_19, stat.dart19Percent)
        val rate20 = StatItem(R.string.stats_field_rate_20, stat.dart20Percent)
        val rateBull = StatItem(R.string.stats_field_rate_bull, stat.dartBullPercent)
        val rateDouble = StatItem(R.string.stats_field_rate_double, stat.doublePercent)
        val rateTriple = StatItem(R.string.stats_field_rate_triple, stat.triplePercent)
        val rates19s20 = StatItem(R.string.stats_field_rate_19_20, stat.dart20Percent + stat.dart19Percent)
        val ratet19t20 = StatItem(R.string.stats_field_rate_t19_t20, stat.triple19Percent +stat.triple20Percent)
        val ratet20 = StatItem(R.string.stats_field_rate_20, stat.triple20Percent)

        val statFieldList = mutableListOf(
            rateMiss, rate1, rate2, rate3, rate4, rate5, rate6, rate7, rate8, rate9,
            rate10, rate11, rate12, rate13, rate14, rate15, rate16, rate17, rate18, rate19,
            rate20, rateBull, rateDouble, rateTriple, rates19s20, ratet19t20, ratet20)

        val fieldsStatsAdapter = FieldsStatsAdapter()
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(requireContext().resources.getDrawable(R.drawable.horizontal_divider, requireContext().theme))
        _binding.gameFieldsRecyclerView.layoutManager = LinearLayoutManager(
            _binding.gameFieldsRecyclerView.context,
            LinearLayoutManager.VERTICAL, false)
        _binding.gameFieldsRecyclerView.addItemDecoration(divider)
        _binding.gameFieldsRecyclerView.adapter = fieldsStatsAdapter
        fieldsStatsAdapter.setData(statFieldList)
    }
}