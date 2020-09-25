package application.android.refresh.ui.user.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import application.android.refresh.R
import application.android.refresh.StartActivity
import application.android.refresh.data.db.UserUtils
import application.android.refresh.ui.cards.CardsViewModel
import application.android.refresh.ui.cards.create.CardsCreateViewModel
import application.android.refresh.ui.cards.create.CardsCreateViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_routines_info.*
import kotlinx.android.synthetic.main.fragment_user_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*
import java.util.regex.Pattern


class UserLoginFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: UserLoginViewModelFactory by
    instance<UserLoginViewModelFactory>()

    private lateinit var viewModel: UserLoginViewModel
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserLoginViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
        userLoginButton.setOnClickListener {
            val username = userLoginUserName.text.toString()
            val password = userLoginPassword.text.toString()
            if (UserUtils.validate(username, password)) {
                login(UserUtils.usernameToEmail(username), password)
            } else {
                userLoginUserNameLayout.error = "Invalid username"
                userLoginPasswordLayout.error = "Invalid password"
            }
        }

        userLoginUserName.setOnFocusChangeListener { _, _ ->
            userLoginUserNameLayout.isErrorEnabled = false
        }

        userLoginPassword.setOnFocusChangeListener { _, _ ->
            userLoginPasswordLayout.isErrorEnabled = false
        }

        userLoginSignup.setOnClickListener {
            findNavController().navigate(R.id.userSignupAction)
        }
    }

    private fun login(username: String, password: String) {
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    setupData()
                } else {
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupData() {
        UserUtils.setUserId(auth.currentUser!!.uid)
        viewModel.getDataFromDB(auth.currentUser!!.uid).observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.addDataToDB()
            } else {
                Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
            }
        })

        viewModel.isDone.observe(viewLifecycleOwner, Observer {
            if (it) {
                (activity as StartActivity).startMainActivity()
            }
        })
    }
}