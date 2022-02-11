package com.decathlon.canaveral.common.binding

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("app:showImage")
fun showImage(imgView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imgView.context).load(url).into(imgView)
    }
}

@BindingAdapter("app:isVisible")
fun isVisible(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("app:showImageRounded")
fun showImageRounded(imgView: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(imgView.context).load(url).centerInside().circleCrop().into(imgView)
    }
}

@BindingAdapter("app:showImageRounded")
fun showImageRounded(imgView: ImageView, drawable: Drawable?) {
    if (drawable != null) {
        Glide.with(imgView.context).load(drawable).centerInside().circleCrop().into(imgView)
    }
}
