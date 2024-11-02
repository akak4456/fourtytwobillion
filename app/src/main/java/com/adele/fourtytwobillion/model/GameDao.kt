package com.adele.fourtytwobillion.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDao {
    @Query("SELECT * FROM game")
    fun getGames(): List<Game>

    @Insert
    fun insertGames(vararg games: Game)

    @Delete
    fun deleteGame(games: List<Game>)
}