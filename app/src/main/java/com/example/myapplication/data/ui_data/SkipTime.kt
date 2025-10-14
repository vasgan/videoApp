package com.example.myapplication.data.ui_data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SkipTime(
    val stop: Int,
    val end: Int,
) : Parcelable