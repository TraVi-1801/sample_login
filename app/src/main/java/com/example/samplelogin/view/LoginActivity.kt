package com.example.samplelogin.view

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.samplelogin.R
import com.example.samplelogin.databinding.ActivityLoginBinding
import com.example.samplelogin.firebase.FirebaseEmailPasswordAuthentication
import com.example.samplelogin.firebase.FirebaseFacebookLogin
import com.example.samplelogin.firebase.FirebaseGoogleAuthentication
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var facebookLogin: FirebaseFacebookLogin
    private lateinit var callbackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // setup facebook login
        FacebookSdk.setAutoLogAppEventsEnabled(true)
        FacebookSdk.setAdvertiserIDCollectionEnabled(true)
        facebookLogin = FirebaseFacebookLogin()
        auth = FirebaseAuth.getInstance()
        callbackManager = CallbackManager.Factory.create()

        binding.imgFb.setOnClickListener {
            facebookLogin.facebookLogin(callbackManager, auth, this)
        }


        // google
        val options = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("730207684145-1mhruoj75t7a58u88j9kc1rf450gp1ge.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, options)

    binding.imgGg.setOnClickListener {
        if (!FirebaseGoogleAuthentication().checkUser()) {
            FirebaseGoogleAuthentication().loginGoogle(userSignIn, mGoogleSignInClient)
        }
    }

        // text span
        textSpanSignIn()

        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }

        binding.btnSignIn.setOnClickListener {
            loginUserWithEmailPassword()
        }

    }

    private fun loginUserWithEmailPassword() {
        if(validateEmail() && validatePassword()) {
            val email = binding.editTextUsername.text.toString().trim{it <= ' '}
            val password = binding.editTextPassword.text.toString().trim{it <= ' '}
            FirebaseEmailPasswordAuthentication().loginUser(email,password,this)
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.editTextUsername.text.toString().trim{it <= ' '}

        if (email.isEmpty()) {
            binding.editTextUsername.requestFocus()
            binding.editTextUsernameLayout.error = "You must enter your email!"
            return false
        }
        if (!isValidEmail(email)) {
            binding.editTextUsername.requestFocus()
            binding.editTextUsernameLayout.error = "Your email is wrong format!"
            return false
        }
        return true
    }

    private fun validatePassword(): Boolean {
        val password = binding.editTextPassword.text.toString().trim{it <= ' '}

        if (password.isEmpty()) {
            binding.editTextPassword.requestFocus()
            binding.editTextPasswordLayout.error = "You must enter your password!"
            return false
        }
        if (password.length < 6) {
            binding.editTextPassword.requestFocus()
            binding.editTextPasswordLayout.error = "Your password length less than 6 character!"
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun textSpanSignIn() {
        val tvCreateAccount : TextView = binding.tvCreateAccount

        val spannableString = SpannableString("Bạn chưa có tài khoản? Đăng ký ngay")

        val fColor = ForegroundColorSpan(Color.parseColor("#333333"))
        spannableString.setSpan(fColor,0,22, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val bColor = ForegroundColorSpan(Color.parseColor("#E94B41"))
        spannableString.setSpan(bColor,23,34, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        tvCreateAccount.text = spannableString
    }

    private val userSignIn =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                    account?.let {
                        FirebaseGoogleAuthentication().googleAuthForFirebase(it, this)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

}