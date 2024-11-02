package com.adele.fourtytwobillion.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GameConverter {
    @TypeConverter
    fun fromArray(array: Array<Array<Long>>): String {
        return Gson().toJson(array)
    }

    @TypeConverter
    fun toArray(data: String): Array<Array<Long>> {
        val listType = object : TypeToken<Array<Array<Long>>>() {}.type
        return Gson().fromJson(data, listType)
    }
}