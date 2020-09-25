package application.android.refresh.data.db

import java.util.regex.Pattern

class UserUtils {

    companion object {
        private var userId: String = ""
        fun getUserId() = userId

        fun setUserId(id: String) {
            userId = id
        }

        fun usernameToEmail(username: String) = "$username@refresh.local"

        fun validate(username: String, password: String): Boolean {
            if (username.length > 32 || password.length > 32 || username.isBlank() || password.isBlank()) return false
            if (!username.matches(Pattern.compile("[A-Za-z0-9]*").toRegex())) return false

            return true
        }
    }

}