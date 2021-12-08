package com.decathlon.canaveral.common

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.BuildConfig
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.utils.CanaveralPreferences
import com.decathlon.canaveral.common.utils.ContextUtils
import com.decathlon.canaveral.game.adapter.KeyboardType
import com.decathlon.canaveral.game.dialog.GameTransitionInfoFragmentArgs
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    protected lateinit var _binding: B
    abstract var layoutId: Int

    protected lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var preferences: CanaveralPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initPreferences()

        firebaseAnalytics = Firebase.analytics
        val parameters = Bundle().apply {
            this.putString("app_version", BuildConfig.VERSION_NAME)
        }
        firebaseAnalytics.setDefaultEventParameters(parameters)

        _binding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)!!
        return _binding.root
    }

    private fun initPreferences() {
        preferences = CanaveralPreferences(requireContext())

        val availableLocales = resources.getStringArray(R.array.languages_code)
        val currentLocale = Locale.getDefault()
        val locale = getCurrentLocale()
        if (locale == null) {
            if (!availableLocales.contains(currentLocale.language)) {
                setCurrentLocale(requireContext(), Locale("en"))
            } else {
                setCurrentLocale(requireContext(), currentLocale)
            }
        } else if (currentLocale.language != locale.language) {
            setCurrentLocale(requireContext(), locale)
        }
    }

    @SuppressLint("Recycle")
    protected fun startScoreAnimation(textView: AppCompatTextView, start: Int, end: Int) {
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.duration = 800
        valueAnimator.interpolator = LinearOutSlowInInterpolator()
        valueAnimator.addUpdateListener {
            textView.text = it.animatedValue.toString()
        }
        valueAnimator.start()
    }

    protected fun showTransitionInfo(actionId: Int, info: String) {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(
                actionId,
                GameTransitionInfoFragmentArgs(info).toBundle())
        }
    }

    protected fun getWaitingPlayersOrdered(
        it: Player,
        allPlayers: List<Player>
    ): ArrayList<Player> {
        val playersWaiting = ArrayList<Player>()
        for (player in allPlayers) {
            if (player != it) {
                if (allPlayers.indexOf(player) < allPlayers.indexOf(it))
                    playersWaiting.add(playersWaiting.size, player)
                else
                    playersWaiting.add(
                        allPlayers.indexOf(player)
                                - allPlayers.indexOf(it) - 1, player
                    )
            }
        }
        return playersWaiting
    }

    fun setCurrentLocale(context: Context, locale: Locale) {
        ContextUtils.updateLocale(context, locale)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)
        preferences.saveLocale(locale)
    }

    fun getCurrentLocale(): Locale? {
        return preferences.getLocale()
    }

    fun refreshCurrentFragment() {
        val id = findNavController().currentDestination?.id
        findNavController().popBackStack(id!!,true)
        findNavController().navigate(id)
    }

    open fun getKeyboardType() = KeyboardType.NORMAL

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