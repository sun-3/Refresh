package application.android.refresh.ui.user.signup

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import application.android.refresh.R
import application.android.refresh.StartActivity
import application.android.refresh.data.db.UserUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_user_signup.*


class UserSignupFragment : Fragment() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        userSignupButton.setOnClickListener {
            val username = userSignupUserName.text.toString()
            val password = userSignupPassword.text.toString()
            if (UserUtils.validate(username, password)) {
                signup(UserUtils.usernameToEmail(username), password)
            } else {
                userSignupUserNameLayout.error = "Invalid username"
                userSignupPasswordLayout.error = "Invalid password"
            }
        }

        userSignupUserName.setOnFocusChangeListener { _, _ ->
            userSignupUserNameLayout.isErrorEnabled = false
        }

        userSignupPassword.setOnFocusChangeListener { _, _ ->
            userSignupPasswordLayout.isErrorEnabled = false
        }
        userSignupLogin.setOnClickListener {
            findNavController().navigate(R.id.userLoginAction)
        }
    }

    private fun signup(username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    UserUtils.setUserId(auth.currentUser!!.uid)
                    (activity as StartActivity).startMainActivity()
                } else {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }
}