package com.example.myapplication.data.ui_data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genres(
    val id: String,
    val name: String
) : Parcelable