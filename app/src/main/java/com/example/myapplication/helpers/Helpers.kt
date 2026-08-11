package com.example.myapplication.helpers

import androidx.compose.ui.text.AnnotatedString
import androidx.core.text.HtmlCompat

fun htmlToAnnotatedString(html: String): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
    return AnnotatedString(spanned.toString())
}