package com.adele.fourtytwobillion

enum class PossiblePosition(val drow: Int, val dcol: Int) {
    UPLEFT(-1, -1),
    UP(-1, 0),
    UPRIGHT(-1,1),
    DOWNLEFT(1,-1),
    DOWN(1, 0),
    DOWNRIGHT(1,1),
    LEFT(0, -1),
    RIGHT(0, 1)
}