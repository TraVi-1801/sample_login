package com.example.samplelogin.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import com.example.samplelogin.databinding.ActivityCreateAccountBinding
import com.example.samplelogin.firebase.FirebaseEmailPasswordAuthentication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CreateAccountActivity : AppCompatActivity() {

    private var _binding: ActivityCreateAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        // text span
        textSpanSignIn()

        // sign in
        binding.tvBackSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener {
            registerUserWithEmailPassword()
        }
    }

    private fun registerUserWithEmailPassword() {
        if( validateEmail() && validatePassword() && validateConfirmPassword()) {
            val email = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()
            FirebaseEmailPasswordAuthentication().registerUser(email,password,this)
        }
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

    private fun validateConfirmPassword(): Boolean {
        val password = binding.editTextConfirmPassword.text.toString().trim{it <= ' '}

        if (password.isEmpty()) {
            binding.editTextConfirmPassword.requestFocus()
            binding.editTextConfirmPasswordLayout.error = "You must enter your password!"
            return false
        }
        if (password.length < 6) {
            binding.editTextConfirmPassword.requestFocus()
            binding.editTextConfirmPasswordLayout.error = "Your password length less than 6 character!"
            return false
        }

        if (password != binding.editTextPassword.text.toString()) {
            binding.editTextPasswordLayout.requestFocus()
            binding.editTextConfirmPasswordLayout.error = "Your confirm password not match your password!"
            return false
        }

        return true
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

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun textSpanSignIn() {
        val tvCreateAccount : TextView = binding.tvBackSignIn

        val spannableString = SpannableString("Bạn đã có tài khoản? Đăng nhập ngay")

        val fColor = ForegroundColorSpan(Color.parseColor("#333333"))
        spannableString.setSpan(fColor,0,20, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val bColor = ForegroundColorSpan(Color.parseColor("#E94B41"))
        spannableString.setSpan(bColor,21,34, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        tvCreateAccount.text = spannableString
    }
}