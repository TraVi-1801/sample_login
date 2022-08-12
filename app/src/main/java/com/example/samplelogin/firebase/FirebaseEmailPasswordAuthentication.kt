package com.example.samplelogin.firebase

import android.content.Intent
import android.widget.Toast
import com.example.samplelogin.view.CreateAccountActivity
import com.example.samplelogin.view.LoginActivity
import com.example.samplelogin.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class FirebaseEmailPasswordAuthentication {

    var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginUser(email: String, password: String, loginActivity: LoginActivity) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(loginActivity.baseContext, "Login User successful!", Toast.LENGTH_SHORT).show()
                    val user = mAuth.currentUser
                    loginActivity.startActivity(Intent(loginActivity.baseContext, MainActivity::class.java))
                    loginActivity.finish()
                } else {
                    Toast.makeText(loginActivity.baseContext, "Login failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                println(it)
            }

    }

    fun registerUser(email: String, password: String,signUpActivity: CreateAccountActivity) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Toast.makeText(signUpActivity.baseContext, "Register User successful!", Toast.LENGTH_SHORT).show()
                    val user = mAuth.currentUser
                    signUpActivity.startActivity(Intent(signUpActivity.baseContext, MainActivity::class.java))
                    signUpActivity.finish()
                } else {
                    when(task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(
                                signUpActivity, "An account already exists with the same email address.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(signUpActivity, "Register User failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                println(it)
            }
    }
}