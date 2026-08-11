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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteScreenViewModel @Inject constructor(
    private val releaseRepository: ReleaseRepository, // todo может пригодиться
    private val favoriteRepository: FavoriteRepository
) : ViewModel()  {
    private val _uiState: MutableStateFlow<Release?> = MutableStateFlow(null)
    private val _uiStateFavorite: MutableStateFlow<List<Favorite>?> = MutableStateFlow(null)
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState: StateFlow<Release?> = _uiState.asStateFlow()
    val favoriteUiState: StateFlow<List<Favorite>?> = _uiStateFavorite.asStateFlow()
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadFavorites(showRefreshing = false)
    }

    fun refresh() {
        loadFavorites(showRefreshing = true)
    }

    private fun loadFavorites(showRefreshing: Boolean) {
        viewModelScope.launch {
            if (showRefreshing) _isRefreshing.value = true
            try {
                val favorites = favoriteRepository.getFavorites()
                _uiStateFavorite.value = favorites
                if (showRefreshing) delay(400)
            } finally {
                if (showRefreshing) _isRefreshing.value = false
            }
        }
    }
}
