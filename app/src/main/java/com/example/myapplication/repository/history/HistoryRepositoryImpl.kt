package com.example.myapplication.repository.history

import com.example.myapplication.data.db.favorite.Favorite
import com.example.myapplication.data.db.favorite.FavoriteDao
import com.example.myapplication.data.db.history.History
import com.example.myapplication.data.db.history.HistoryDao
import com.example.myapplication.repository.favorite.FavoriteRepository

class HistoryRepositoryImpl (val historyDb: HistoryDao) : HistoryRepository {

/*    override suspend fun addFavorite(id: Int, name: String) {
        favoriteDB.upsert(Favorite(id, name))
    }

    override suspend fun removeFavorite(id: Int) {
        favoriteDB.deleteById(id)
    }

    override suspend  fun getFavorites(): List<Favorite>? {
        return favoriteDB.getFavorites()
    }

    override suspend fun getFavorite(id: Int): Favorite? {
        return favoriteDB.getFavorite(id)
    }*/

    override suspend fun addToHistory(history: History) {
        historyDb.upsert(history)
    }

    override suspend fun getHistory(): List<History>? {
        return historyDb.getHistories()
    }
}