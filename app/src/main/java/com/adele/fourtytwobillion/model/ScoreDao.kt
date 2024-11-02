package com.adele.fourtytwobillion.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Query("SELECT * FROM score")
    fun getScores(): List<Score>

    @Insert
    fun insertScores(vararg scores: Score)

    @Delete
    fun deleteScores(scores: List<Score>)
}