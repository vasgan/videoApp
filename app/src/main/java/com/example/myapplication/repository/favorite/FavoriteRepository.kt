package com.example.myapplication.repository.favorite

import com.example.myapplication.data.db.favorite.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    suspend fun addFavorite(id: Int, name: String, imageUrl: String)

    suspend fun removeFavorite(id: Int)

    suspend fun getFavorites(): List<Favorite>?

    fun getFavorite(id: Int): Flow<Favorite>
}