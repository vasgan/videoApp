package com.example.myapplication.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTopBar(idString: Int, onBackButtonClick: () -> Unit) {
    TopAppBar(
        title = { Text(LocalContext.current.getString(idString)) },
        navigationIcon = {
            IconButton(onClick = { onBackButtonClick() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
            }
        }
    )
}