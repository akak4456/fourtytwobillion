package com.adele.fourtytwobillion.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Game::class, Score::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun scoreDao(): ScoreDao
}