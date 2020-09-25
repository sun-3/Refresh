package application.android.refresh

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val navController = findNavController(R.id.nav_start_host_fragment)
        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }
    }

    fun startMainActivity() {
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        startActivity(mainActivityIntent)
        finish()
    }

    override fun onBackPressed() {
        finish()
    }
}