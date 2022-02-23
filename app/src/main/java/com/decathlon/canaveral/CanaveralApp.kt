package com.decathlon.canaveral

import android.app.Application
import com.decathlon.canaveral.common.interactors.Interactors
import com.decathlon.canaveral.common.interactors.player.AddPlayer
import com.decathlon.canaveral.common.interactors.player.DeletePlayer
import com.decathlon.canaveral.common.interactors.player.GetPlayers
import com.decathlon.canaveral.common.interactors.player.UpdatePlayer
import com.decathlon.canaveral.common.interactors.user.*
import com.decathlon.canaveral.dashboard.DashboardViewModel
import com.decathlon.canaveral.game.countup.CountUpViewModel
import com.decathlon.canaveral.game.x01.Game01ViewModel
import com.decathlon.canaveral.intro.IntroViewModel
import com.decathlon.canaveral.intro.UserConsentManager
import com.decathlon.canaveral.player.PlayerEditionViewModel
import com.decathlon.core.Constants
import com.decathlon.core.player.data.PlayerRepository
import com.decathlon.core.player.data.source.RoomPlayerDataSource
import com.decathlon.core.user.data.STDRepository
import com.decathlon.core.user.data.UserRepository
import com.decathlon.core.user.data.source.RoomUserDataSource
import com.decathlon.core.user.data.source.datastore.AccountPreference
import com.decathlon.core.user.data.source.network.STDServices
import com.decathlon.decathlonlogin.DktLoginManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
                networkApiModule,
                interactorsModule
            ))
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private val sdkManagersModule = module {
        single { DktLoginManager.getInstance(androidContext()) }
        single { UserConsentManager(this@CanaveralApp) }
    }

    private val repositoriesModule = module {
        single { AccountPreference(androidContext()) }
        single { PlayerRepository(RoomPlayerDataSource(this@CanaveralApp)) }
        single { UserRepository(RoomUserDataSource(this@CanaveralApp), get(), get(), get()) }
        factory { STDRepository(get()) }
    }

    private val networkApiModule = module {
        single {
            Retrofit.Builder()
                .baseUrl(Constants.STD_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        single {
            get<Retrofit>().create(STDServices::class.java)
        }
    }

    private val viewModelsModule = module {
        viewModel { DashboardViewModel(get()) }
        viewModel { PlayerEditionViewModel(get()) }
        viewModel { Game01ViewModel(get()) }
        viewModel { CountUpViewModel(get()) }
        viewModel { IntroViewModel(get(), get()) }
    }

    private val interactorsModule = module {
        factory {
            Interactors(
                GetPlayers(get()),
                AddPlayer(get()),
                DeletePlayer(get()),
                UpdatePlayer(get()),
                InitLogin(get()),
                UserLogin(get()),
                UserLoginState(get()),
                GetUserInfo(get()),
                CompleteUserInfo(get()),
                GetUsers(get()),
                AddUser(get()),
                UpdateUser(get()),
                UserConsent(get()),

            )
        }
    }
}


