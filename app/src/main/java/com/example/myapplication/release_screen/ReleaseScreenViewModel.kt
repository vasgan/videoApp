package com.example.myapplication.release_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.db.favorite.Favorite
import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.EpisodeResponse
import com.example.myapplication.data.network.responses.FranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.GenresResponse
import com.example.myapplication.data.ui_data.Episode
import com.example.myapplication.data.ui_data.Franchise
import com.example.myapplication.data.ui_data.Genres
import com.example.myapplication.data.ui_data.Release
import com.example.myapplication.data.ui_data.SkipTime
import com.example.myapplication.repository.favorite.FavoriteRepository
import com.example.myapplication.repository.video.ReleaseRepository
import com.example.myapplication.repository.watch_list.WatchListRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReleaseScreenViewModel @Inject constructor(
    private val repository: ReleaseRepository,
    private val favoriteRepository: FavoriteRepository,
    private val watchListRepository: WatchListRepository
) : ViewModel() {
    private val _uiState: MutableStateFlow<Release?> = MutableStateFlow(null)
    private val _uiStateWatchList: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _uiStateFavorite: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val uiState: StateFlow<Release?> = _uiState.asStateFlow()
    val favoriteUiState: StateFlow<Boolean> = _uiStateFavorite.asStateFlow()
    val watchListUIState: StateFlow<Boolean> = _uiStateWatchList.asStateFlow()
    fun getCurrentRelease(id: String) {
        viewModelScope.launch {
            _uiStateWatchList.value = watchListRepository.getWatchItem(id.toInt()) != null
            mapFranchises(
                async { repository.getRelease(id) }.await(),
                async { repository.getFranchises(id) }.await(),
                async { favoriteRepository.getFavorite(id.toInt()) }.await()
            )
        }
    }

    private fun mapFranchises(
        release: FullReleaseResponse?,
        franchiseResponse: List<BaseFranchiseResponse>?,
        favorite: Favorite?
    ) {
        _uiStateFavorite.value = favorite != null
        if (release == null) return
        val franchises =
            if (franchiseResponse == null || franchiseResponse.isEmpty()) mutableListOf()
            else franchiseResponse[0].franchiseReleases
        val value = Release(
            release.id,
            release.name.main,
            release.poster.preview,
            release.description,
            release.year + " " + release.season.description,
            mapGenres(release.genres),
            release.ageRating.value,
            release.type.description + " (" + release.episodes.size + ")," + release.averageDurationOfEpisode + "мин",
            (release.averageDurationOfEpisode).toString(),
            mapFranchise(franchises),
            mapEpisodes(release.episodes),
            favorite = favorite != null
        )
        _uiState.value = value
    }

    private fun mapGenres(list: List<GenresResponse>): List<Genres> {
        val genres = mutableListOf<Genres>()
        list.forEach {
            genres.add(
                Genres(
                    it.id,
                    it.name
                )
            )
        }
        return genres
    }

    private fun mapFranchise(franchise: List<FranchiseResponse>?): List<Franchise> {
        val value = mutableListOf<Franchise>()
        franchise?.forEach {
            value.add(
                Franchise(
                    it.id ?: "",
                    it.sortOrder ?: 0,
                    it.releaseId ?: "",
                    it.franchiseId ?: "",
                    it.release?.name?.main ?: ""
                )
            )
        }
        return value
    }

    private fun mapEpisodes(episodes: List<EpisodeResponse>): List<Episode> {
        val value = mutableListOf<Episode>()
        episodes.forEach {
            value.add(
                Episode(
                    it?.name ?: "",
                    it.id,
                    it.ordinal,
                    it.hls480 ?: "",
                    it.hls720 ?: "",
                    it.hls1080 ?: "",
                    it.duration,
                    SkipTime(it.opening?.start ?: 0, it.opening?.stop ?: 0),
                    SkipTime(it.ending?.start ?: 0, it.ending?.stop ?: 0),
                    it.preview.thumbnail ?: "",
                    it.sortOrder,
                    it.releaseId.toString()
                )
            )
        }
        return value
    }

    fun changeFavorite() {
        viewModelScope.launch {
            val id = _uiState.value?.id?.toInt()
            val name = _uiState.value?.name
            if (id != null && name != null)
                if (!_uiStateFavorite.value) {
                    favoriteRepository.addFavorite(id, name)
                    _uiStateFavorite.value = true
                } else {
                    favoriteRepository.removeFavorite(id)
                    _uiStateFavorite.value = false
                }
        }
    }

    fun changeWatchList() {
        viewModelScope.launch {
            val id = _uiState.value?.id?.toInt()
            val name = _uiState.value?.name
            if (id != null && name != null)
                if (!_uiStateWatchList.value) {
                    watchListRepository.addToWatchList(id, name)
                    _uiStateWatchList.value = true
                } else {
                    watchListRepository.removeFromWatchList(id)
                    _uiStateWatchList.value = false
                }
        }
    }
}