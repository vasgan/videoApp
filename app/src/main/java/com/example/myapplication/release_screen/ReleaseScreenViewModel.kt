package com.example.myapplication.release_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.myapplication.video.VideoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReleaseScreenViewModel @Inject constructor(
    private val repository: VideoRepository,
) : ViewModel() {
    private val _uiState: MutableStateFlow<Release?> = MutableStateFlow(null)
    val uiState: StateFlow<Release?> = _uiState.asStateFlow()

    fun getCurrentRelease(id: String) {
        viewModelScope.launch {
            mapFranchises(
                async { repository.getRelease(id) }.await(),
                async { repository.getFranchises(id) }.await()//todo сделать парсинг франчайза
            )
        }
    }

    private fun mapFranchises(
        release: FullReleaseResponse?,
        franchiseResponse: List<BaseFranchiseResponse>?
    ) {
        if (release == null) return
        val franchises =
            if (franchiseResponse == null || franchiseResponse.isEmpty()) mutableListOf<FranchiseResponse>()
            else franchiseResponse[0].franchiseReleases
        val value = Release(
            release.id,
            release.name.main,
            (release.poster.src).toString(),
            release.description,
            release.year + " " + release.season.description,
            mapGenres(release.genres),
            release.ageRating.value,
            release.type.description + " (" + release.episodes.size + ")," + release.averageDurationOfEpisode + "мин",
            (release.averageDurationOfEpisode).toString(),
            mapFranchise(franchises),
            mapEpisodes(release.episodes),
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
                    it.preview.thumbnail?: "",
                    it.sortOrder,
                    it.releaseId.toString()
                )
            )
        }
        return value
    }
}