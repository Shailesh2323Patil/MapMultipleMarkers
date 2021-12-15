package com.example.mapmultiplemarker

import androidx.annotation.DrawableRes

class MarkerData(
    val latitutde: Double,
    val longitude: Double,
    val title: String,
    val snippets: String,
    @DrawableRes val iconResID: Int? = null
)