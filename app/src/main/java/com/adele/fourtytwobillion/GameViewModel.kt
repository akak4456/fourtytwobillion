package com.adele.fourtytwobillion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    val board: MutableLiveData<Array<Array<Long>>> = MutableLiveData(
        arrayOf(
            arrayOf(0,0,0,0,0),
            arrayOf(0,0,0,0,0),
            arrayOf(0,0,123456789123L,0,0),
            arrayOf(0,0,0,0,0),
            arrayOf(0,0,0,0,0),
        )
    )
    val score: MutableLiveData<Long> = MutableLiveData(0L)
}