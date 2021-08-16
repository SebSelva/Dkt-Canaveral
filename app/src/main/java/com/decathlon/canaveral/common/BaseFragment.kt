package com.decathlon.canaveral.common

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import com.decathlon.canaveral.R
import com.decathlon.canaveral.game.dialog.GameTransitionInfoFragmentArgs

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    protected lateinit var _binding: B
    abstract var layoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)!!
        return _binding.root
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

    protected fun showTransitionInfo(info: String) {
        this.findNavController().navigate(
            R.id.action_game_to_score,
            GameTransitionInfoFragmentArgs(info).toBundle())
    }
}