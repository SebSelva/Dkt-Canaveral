package com.decathlon.canaveral

import android.app.Application
import androidx.lifecycle.AndroidViewModel

open class CanaveralViewModel (application: Application, protected val interactors: Interactors) :
    AndroidViewModel(application) {

    protected val application: CanaveralApp = getApplication()

}