package application.android.refresh.data.db

import androidx.room.TypeConverter
import application.android.refresh.data.internal.PracticeCard

class Converters {

    @TypeConverter
    fun longListToString(longList: List<Long>): String {
        if (longList.isEmpty()) { return "" }
        return longList.joinToString(",")
    }

    @TypeConverter
    fun stringToLongList(str: String): List<Long> {
        if (str.isEmpty()) { return listOf() }
        return str.split(",").map {
            it.toLong()
        }
    }

    @TypeConverter
    fun routineCardToJson(practiceCardList: MutableList<PracticeCard>): String {
        return DbUtils.GSON().toJson(practiceCardList)
    }

    @TypeConverter
    fun jsonToRoutineCard(value: String): MutableList<PracticeCard> {
        return DbUtils.GSON().fromJson(
            value,
            Array<PracticeCard>::class.java).toMutableList()
    }

}