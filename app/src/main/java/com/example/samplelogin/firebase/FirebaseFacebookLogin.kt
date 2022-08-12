package com.example.samplelogin.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistryOwner
import com.example.samplelogin.view.MainActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class FirebaseFacebookLogin {
    fun facebookLogin(
        callbackManager: CallbackManager,
        auth: FirebaseAuth,
        context: Context
    ) {
        LoginManager.getInstance().logInWithReadPermissions(
            context as ActivityResultRegistryOwner,
            callbackManager,
            listOf("email", "public_profile", "user_friends")
        )
        println("Facebook Login")
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                println("facebook:onSuccess:$result")
                handleFacebookAccessToken(result.accessToken, context as Activity, auth)
            }

            override fun onCancel() {
                println("facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(
                    context, error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun handleFacebookAccessToken(token: AccessToken, activity: Activity, auth: FirebaseAuth) {
        println("handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in User's information
                    println("signInWithCredential:success")
                    activity.startActivity(Intent(activity.baseContext,MainActivity::class.java))
                    activity.finish()
                    val user = auth.currentUser
                    println(user?.displayName)

                    Toast.makeText(
                        activity.baseContext, "Facebook login successful",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    // If sign in fails, display a message to the User.
                    println("signInWithCredential:failure  ${task.exception}")
                    when(task.exception) {
                        is FirebaseAuthUserCollisionException -> {
                            Toast.makeText(
                                activity.baseContext, "An account already exists with the same email address.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                activity.baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    fun facebookLoginSignOut() {
        LoginManager.getInstance().logOut()
    }
}