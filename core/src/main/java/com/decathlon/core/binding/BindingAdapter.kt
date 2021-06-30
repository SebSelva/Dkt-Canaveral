package com.decathlon.core.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("app:showImage")
fun showImage(imgView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imgView.context).load(url).into(imgView)
    }
}
