package com.adele.fourtytwobillion

enum class PossiblePosition(val drow: Int, val dcol: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1)
}