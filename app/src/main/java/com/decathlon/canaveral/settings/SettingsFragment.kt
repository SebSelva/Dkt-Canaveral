package com.decathlon.canaveral.settings

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.decathlon.canaveral.R
import com.decathlon.canaveral.common.BaseFragment
import com.decathlon.canaveral.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment: BaseFragment<FragmentSettingsBinding>() {

    override var layoutId: Int = R.layout.fragment_settings

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val languages = resources.getStringArray(R.array.languages_trad)
        val languageCodes = resources.getStringArray(R.array.languages_code)
        var langIndex = languageCodes.indexOf(getCurrentLocale()!!.language)

        val languagesAdapter = SettingsItemListAdapter {
            langIndex = it
            _binding.settingsLanguage.text = languages[langIndex]
            toggleItemList(_binding.languageRecyclerview, false)
            setCurrentLocale(view.context, Locale(languageCodes[langIndex]))
        }
        _binding.languageRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        _binding.languageRecyclerview.adapter = languagesAdapter
        languagesAdapter.setData(languages.asList(), languages[langIndex])
        toggleItemList(_binding.languageRecyclerview, true) // Hide list at startup

        _binding.settingsLanguage.text = languages[langIndex]
        _binding.settingsLanguage.setOnClickListener {
            // Open recycler view
            (_binding.languageRecyclerview.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(if (langIndex > 1) langIndex-1 else 0, 0)
            toggleItemList(_binding.languageRecyclerview, false)
        }
    }

    private fun toggleItemList(view: View, forceClose: Boolean) {
        val animationDuration = resources.getInteger(R.integer.animation_duration).toLong()

        val isOpened = view.isVisible
        if (isOpened || forceClose) {
            view.animate().apply {
                duration = animationDuration
                translationY(-view.height.toFloat())
                alpha(0F)
                setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.isVisible = false
                    }
                })
            }
        } else {
            view.isVisible = true
            view.animate().apply {
                duration = animationDuration
                translationY(0F)
                alpha(1F)
                setListener(null)
            }
        }
    }
}