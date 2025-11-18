package com.example.myapplication.profile_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun ProfileScreen(
    innerPadding: PaddingValues,
    onFavoriteClick: () -> Unit,
    onWillWatchClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val localContext = LocalContext.current
    Column(modifier = Modifier.padding(innerPadding)) {

        ItemProfileElement(text = localContext.getString(R.string.favorite)) {
            onFavoriteClick()
        }
        ItemProfileElement(text = localContext.getString(R.string.views_plans)) {
            onWillWatchClick()
        }
        ItemProfileElement(text = localContext.getString(R.string.history)) {
            onHistoryClick()
        }
        ItemProfileElement(text = localContext.getString(R.string.settings)) {
            onSettingsClick()
        }
    }
}

@Composable
fun ItemProfileElement(text: String, click: () -> Unit) {
    Box(Modifier.fillMaxWidth().clickable {
        click()
    }) {
        Text(modifier = Modifier.padding(16.dp), text = text)
    }
}