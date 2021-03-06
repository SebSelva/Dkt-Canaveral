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
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.model.Player
import com.decathlon.canaveral.common.utils.*
import com.decathlon.canaveral.game.adapter.KeyboardType
import com.decathlon.canaveral.game.dialog.GameTransitionInfoFragmentArgs
import com.decathlon.canaveral.intro.UserConsentManager
import org.koin.android.ext.android.inject

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    protected lateinit var _binding: B
    abstract var layoutId: Int

    private val consentManager: UserConsentManager by inject()
    var firebaseManager: FirebaseManager? = null
    private lateinit var preferences: CanaveralPreferences
    lateinit var localeUtils: LocaleUtils

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init()

        _binding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)!!
        return _binding.root
    }

    private fun init() {
        firebaseManager = FirebaseManager()
        preferences = CanaveralPreferences(requireContext())
        localeUtils = LocaleUtils(requireContext(), preferences)
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

    fun refreshCurrentFragment() {
        val id = findNavController().currentDestination?.id
        findNavController().popBackStack(id!!,true)
        findNavController().navigate(id)
    }

    open fun getKeyboardType() = KeyboardType.NORMAL

    // Consent action to User
    protected suspend fun userConsentAction() {
        consentManager.initDidomi()
        consentManager.didomiControl(requireActivity())
    }

    fun getNetworkStatus(): Boolean {
        return if (!isNetworkAvailable(requireContext())) {
            showSnackBar(getString(R.string.common_internet_error))
            false
        }
        else true
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