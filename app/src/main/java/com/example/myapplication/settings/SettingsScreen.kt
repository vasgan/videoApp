package com.example.myapplication.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.R
import com.example.myapplication.ui.SimpleTopBar

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            SimpleTopBar(R.string.settings) { onBackClick() }
        }, content = { innerPaddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPaddingValues)
                    .fillMaxSize()
            ) {

            }
        }
    )
}