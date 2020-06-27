package application.android.refresh.data.db

import com.google.gson.Gson

object DbUtils {
    private val gson = Gson()
    fun GenerateId(): Long {
        return System.currentTimeMillis()
    }

    fun GSON() = gson
}