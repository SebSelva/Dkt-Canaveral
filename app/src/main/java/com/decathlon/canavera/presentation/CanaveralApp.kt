package com.decathlon.canavera.presentation;

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CanaveralApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CanaveralApp)
            modules(/**Add module Core, Presentation, Data**/)
        }
    }
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun ViewGroup.inflateLayout(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}



