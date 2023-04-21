package com.onurerdem.shoppingapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.onurerdem.shoppingapp.data.remote.utils.Constants
import kotlin.math.round

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(view: ImageView, image: String) {
        Glide.with(view.context)
            .load(image)
            .into(view)
    }
    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}