package com.decathlon.canaveral

import android.app.Application
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.canaveral.common.interactors.player.PlayerActions
import com.decathlon.canaveral.common.interactors.stats.StdActions
import com.decathlon.canaveral.common.interactors.user.*
import com.decathlon.canaveral.dashboard.DashboardViewModel
import com.decathlon.canaveral.game.GameStatsViewModel
import com.decathlon.canaveral.game.countup.CountUpViewModel
import com.decathlon.canaveral.game.x01.Game01ViewModel
import com.decathlon.canaveral.intro.LoginViewModel
import com.decathlon.canaveral.intro.UserConsentManager
import com.decathlon.canaveral.player.PlayerEditionViewModel
import com.decathlon.canaveral.stats.StatsViewModel
import com.decathlon.canaveral.user.UserEditionViewModel
import com.decathlon.core.Constants
import com.decathlon.core.gamestats.data.STDRepository
import com.decathlon.core.gamestats.data.source.room.LocalActivitiesDataSource
import com.decathlon.core.gamestats.data.source.room.RoomStatsDataSource
import com.decathlon.core.player.data.PlayerRepository
import com.decathlon.core.player.data.source.RoomPlayerDataSource
import com.decathlon.core.user.data.UserRepository
import com.decathlon.core.user.data.source.RoomUserDataSource
import com.decathlon.core.user.data.source.datastore.AccountPreference
import com.decathlon.decathlonlogin.DktLoginManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import timber.log.Timber

class CanaveralApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CanaveralApp)
            modules(listOf(
                repositoriesModule,
                sdkManagersModule,
                viewModelsModule,
                interactorsModule
            ))
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private val sdkManagersModule = module {
        single { DktLoginManager.getInstance(this@CanaveralApp) }
        single { UserConsentManager(this@CanaveralApp) }
    }

    private val repositoriesModule = module {
        // Databases
        single { AccountPreference(androidContext()) }
        single { RoomPlayerDataSource(this@CanaveralApp) }
        single { RoomUserDataSource(this@CanaveralApp) }
        single { RoomStatsDataSource(this@CanaveralApp) }
        single { LocalActivitiesDataSource(this@CanaveralApp) }
        // Repositories
        single { PlayerRepository(get() as RoomPlayerDataSource) }
        single { UserRepository(get() as RoomUserDataSource, get(), get(), get() as RoomStatsDataSource) }
        single { STDRepository(Constants.STD_KEY, get() as RoomStatsDataSource, get()) }
    }

    private val viewModelsModule = module {
        viewModel { DashboardViewModel(get()) }
        viewModel { PlayerEditionViewModel(get()) }
        viewModel { Game01ViewModel(get()) }
        viewModel { CountUpViewModel(get()) }
        viewModel { LoginViewModel(get(), get()) }
        viewModel { UserEditionViewModel(get()) }
        viewModel { StatsViewModel(get()) }
        viewModel { GameStatsViewModel() }
    }

    private val interactorsModule = module {
        factory {
            Interactors(
                PlayerActions(get()),
                InitLogin(get()),
                UserLogin(get()),
                UserLogout(get()),
                UserLoginState(get()),
                GetUserInfo(get()),
                CompleteUserInfo(get()),
                UserActions(get()),
                UserConsent(get()),
                StdActions(get()),
            )
        }
    }
}


