package com.example.myapplication.repository.favorite

import com.example.myapplication.data.db.favorite.Favorite
import com.example.myapplication.data.db.favorite.FavoriteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteRepositoryImpl(val favoriteDB: FavoriteDao) : FavoriteRepository {

    override suspend fun addFavorite(id: Int, name: String, imageUrl: String, doubers: String, url: String?) {
        favoriteDB.upsert(Favorite(id, name, imageUrl, doubers, url))
    }

    override suspend fun removeFavorite(id: Int) {
        favoriteDB.deleteById(id)
    }

    override suspend  fun getFavorites(): List<Favorite> {
        return favoriteDB.getFavorites()
    }

    override fun getFavorite(id: Int): Flow<Favorite> {
        return flow {
            val favorite = favoriteDB.getFavorite(id)
            if (favorite != null)
                emit(favorite)
            else
                emit(Favorite(-1, "", "", ""))
        }
    }
}