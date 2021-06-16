package com.decathlon.canaveral

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object CanaveralViewModelFactory : ViewModelProvider.Factory {

    lateinit var application: Application

    lateinit var dependencies: Interactors

    fun inject(application: Application, dependencies: Interactors) {
        CanaveralViewModelFactory.application = application
        CanaveralViewModelFactory.dependencies = dependencies
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(CanaveralViewModel::class.java.isAssignableFrom(modelClass)) {
            return modelClass.getConstructor(Application::class.java, Interactors::class.java)
                .newInstance(
                    application,
                    dependencies)
        } else {
            throw IllegalStateException("ViewModel must extend MajesticViewModel")
        }
    }

}