package com.onurerdem.shoppingapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.onurerdem.shoppingapp.data.remote.utils.Constants

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(view: ImageView, url: String?) {
        Glide.with(view.context)
            .load("${Constants.BASE_IMAGE_URL}$url")
            .into(view)
    }
}