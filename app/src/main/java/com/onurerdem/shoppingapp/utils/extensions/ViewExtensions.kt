package com.onurerdem.shoppingapp.utils.extensions

import android.view.View

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.changeVisibility(isVisible: Boolean) {
    if (isVisible) {
        visible()
    } else {
        gone()
    }
}