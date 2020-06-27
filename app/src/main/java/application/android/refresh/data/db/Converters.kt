package application.android.refresh.data.db

import androidx.room.TypeConverter
import application.android.refresh.data.db.entity.RoutineCard

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
    fun routineCardToJson(routineCardList: MutableList<RoutineCard>): String {
        return DbUtils.GSON().toJson(routineCardList)
    }

    @TypeConverter
    fun jsonToRoutineCard(value: String): MutableList<RoutineCard> {
        return DbUtils.GSON().fromJson(
            value,
            Array<RoutineCard>::class.java).toMutableList()
    }

}