package com.example.myapplication.favorite_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.favorite.Favorite
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.repository.favorite.FavoriteRepository
import com.example.myapplication.repository.video.ReleaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteScreenViewModel @Inject constructor(
    private val releaseRepository: ReleaseRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel()  {
    private val _uiState: MutableStateFlow<Release?> = MutableStateFlow(null)
    private val _uiStateFavorite: MutableStateFlow<List<Favorite>?> = MutableStateFlow(null)
    val uiState: StateFlow<Release?> = _uiState.asStateFlow()
    val favoriteUiState: StateFlow<List<Favorite>?> = _uiStateFavorite.asStateFlow()

    fun getFavorite() {
        viewModelScope.launch {
            val favorites = favoriteRepository.getFavorites()
            _uiStateFavorite.value = favorites
        }
    }
}