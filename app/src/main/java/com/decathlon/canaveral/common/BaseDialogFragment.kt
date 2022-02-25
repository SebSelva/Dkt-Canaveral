package com.decathlon.canaveral.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.decathlon.canaveral.common.utils.CanaveralPreferences
import com.decathlon.canaveral.common.utils.FirebaseManager
import com.decathlon.canaveral.common.utils.LocaleUtils

abstract class BaseDialogFragment<B : ViewDataBinding> : DialogFragment() {

    protected lateinit var _binding: B
    abstract var layoutId: Int

    private lateinit var preferences: CanaveralPreferences
    lateinit var localeUtils: LocaleUtils
    var firebaseManager :FirebaseManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDialog()

        _binding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
        return _binding.root
    }

    private fun initDialog() {
        firebaseManager = FirebaseManager()
        preferences = CanaveralPreferences(requireContext())
        localeUtils = LocaleUtils(requireContext(), preferences)
    }
}