package com.decathlon.canaveral;

import android.app.Application
import com.decathlon.canaveral.common.interactors.AddPlayer
import com.decathlon.canaveral.common.interactors.DeletePlayer
import com.decathlon.canaveral.common.interactors.GetPlayers
import com.decathlon.canaveral.common.interactors.UpdatePlayer
import com.decathlon.canaveral.dashboard.DashboardViewModel
import com.decathlon.canaveral.game.countup.CountUpViewModel
import com.decathlon.canaveral.game.x01.Game01ViewModel
import com.decathlon.core.player.data.PlayerRepository
import com.decathlon.core.player.data.source.RoomPlayerDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import timber.log.Timber

class CanaveralApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CanaveralApp)
            modules(listOf(repositoriesModule,viewModelsModule,interactorsModule))
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private val repositoriesModule = module {
        single { PlayerRepository(RoomPlayerDataSource(context = this@CanaveralApp)) }
    }

    private val viewModelsModule = module {
        viewModel { DashboardViewModel(get()) }
        viewModel { Game01ViewModel(get()) }
        viewModel { CountUpViewModel(get()) }
    }

    private val interactorsModule = module {
        factory {
            Interactors(
                GetPlayers(get()),
                AddPlayer(get()),
                DeletePlayer(get()),
                UpdatePlayer(get())
            )
        }
    }
}


