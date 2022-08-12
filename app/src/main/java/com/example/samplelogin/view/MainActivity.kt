package com.example.samplelogin.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.samplelogin.databinding.ActivityMainBinding
import com.example.samplelogin.firebase.FirebaseFacebookLogin
import com.example.samplelogin.firebase.FirebaseGoogleAuthentication
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth
        // Google
        val options = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken("730207684145-1mhruoj75t7a58u88j9kc1rf450gp1ge.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, options)


        binding.btnSignOut.setOnClickListener {
            Firebase.auth.signOut()
            FirebaseGoogleAuthentication().signOut(this, mGoogleSignInClient)
            FirebaseFacebookLogin().facebookLoginSignOut()
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }
}