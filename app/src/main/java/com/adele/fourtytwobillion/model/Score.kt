package com.adele.fourtytwobillion.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Score(
    @PrimaryKey val id: String = "high-score",
    val highScore: Long
)