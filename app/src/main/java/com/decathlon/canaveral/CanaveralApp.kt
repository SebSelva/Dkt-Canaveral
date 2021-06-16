package com.decathlon.canaveral;

import android.app.Application
import com.decathlon.core.player.data.PlayerRepository
import com.decathlon.core.player.data.source.RoomPlayerDataSource
import com.decathlon.core.player.interactors.AddPlayer
import com.decathlon.core.player.interactors.GetPlayers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class CanaveralApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initModules()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CanaveralApp)
            modules(/** **/)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initModules() {
        initDashboard()
    }

    private fun initDashboard() {
        val playerRepository = PlayerRepository(RoomPlayerDataSource(this))
        CanaveralViewModelFactory.inject(
            this,
            Interactors(
                GetPlayers(playerRepository),
                AddPlayer(playerRepository)
            )
        )
    }
}


