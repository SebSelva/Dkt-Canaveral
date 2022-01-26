package com.decathlon.canaveral.common.utils

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackBar(text: String) {
    Snackbar.make(
        requireActivity().findViewById(android.R.id.content),
        text, Snackbar.LENGTH_LONG
    ).show()
}