package com.adele.fourtytwobillion.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity
@TypeConverters(GameConverter::class)
data class Game(
    @PrimaryKey val id: String = "game",
    val curScore: Long,
    val board: Array<Array<Long>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (id != other.id) return false
        if (!board.contentDeepEquals(other.board)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + board.contentDeepHashCode()
        return result
    }
}
