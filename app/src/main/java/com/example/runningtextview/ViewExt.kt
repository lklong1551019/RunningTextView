package com.example.runningtextview

import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes

fun View.getDimension(@DimenRes id: Int): Int {
    return resources.getDimensionPixelSize(id)
}

fun View.getDimensionInFloat(@DimenRes id: Int): Float {
    return resources.getDimension(id)
}

fun View.getColorWithoutTheme(@ColorRes id: Int): Int {
    return if (AppUtil.hasMarshmallow())
        resources.getColor(id, null)
    else
        resources.getColor(id)
}