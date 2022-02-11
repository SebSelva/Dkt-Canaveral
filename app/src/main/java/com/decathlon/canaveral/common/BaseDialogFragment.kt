package com.decathlon.canaveral.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
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

    /**
     * Workaround to always have dropdown menu choice list filled
     */
    class MySpinnerAdapter(context: Context, layoutId: Int, items: Array<String>)
        : ArrayAdapter<String>(context, layoutId, items) {

        private val noOpFilter = object : Filter() {
            private val noOpResult = FilterResults()
            override fun performFiltering(constraint: CharSequence?) = noOpResult
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Workaround to not show filtered results
            }
        }

        override fun getFilter() = noOpFilter
    }
}