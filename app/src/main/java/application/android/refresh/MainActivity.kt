package application.android.refresh

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import application.android.refresh.data.db.UserUtils
import application.android.refresh.data.db.entity.Card
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val startActivityIntent = Intent(applicationContext, StartActivity::class.java)
        startActivity(startActivityIntent)
        finish()
    }

    fun deleteAccount() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null && auth.currentUser!!.uid == UserUtils.getUserId()) {
            db.collection("data").document(UserUtils.getUserId()).delete()
            val user = auth.currentUser!!
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signOut()
                    }
                }
        }

    }

    fun confirmDeleteDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Delete Account")
        builder.setMessage("This will delete all your data.")
            .setPositiveButton(
                "Delete"
            ) { _, _ ->
                deleteAccount()
            }
            .setNegativeButton(
                "Cancel"
            ) { _, _ ->
            }
        builder.create()
        builder.show()
    }
}
