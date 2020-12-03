package com.example.runningtextview

import android.os.Build

internal class AppUtil {
    companion object {
        fun hasLollipopMr1(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
        fun hasMarshmallow(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        fun hasNougat()     : Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
        fun hasOreo()       : Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        fun hasPie()        : Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
}