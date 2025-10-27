package com.example.myapplication.repository.favorite

import com.example.myapplication.data.db.favorite.Favorite
import com.example.myapplication.data.db.favorite.FavoriteDao

class FavoriteRepositoryImpl(val favoriteDB: FavoriteDao) : FavoriteRepository {

    override suspend fun addFavorite(id: Int, name: String, imageUrl: String) {
        favoriteDB.upsert(Favorite(id, name, imageUrl))
    }

    override suspend fun removeFavorite(id: Int) {
        favoriteDB.deleteById(id)
    }

    override suspend  fun getFavorites(): List<Favorite>? {
        return favoriteDB.getFavorites()
    }

    override suspend fun getFavorite(id: Int): Favorite? {
        return favoriteDB.getFavorite(id)
    }
}