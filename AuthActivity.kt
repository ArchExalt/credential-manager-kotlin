// USER SIGN-IN

package com.exalt.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.Base64

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    private fun generateNonce(): String {
        val nonceBytes = ByteArray(16)
        SecureRandom().nextBytes(nonceBytes)
        return Base64.getEncoder().withoutPadding().encodeToString(nonceBytes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize CredentialManager
        credentialManager = CredentialManager.create(this)

        val webClientId = getString(R.string.default_web_client_id) // default web client ID can be found in Firebase settings; it's saved as a string in strings.xml

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .setNonce(generateNonce())
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val sharedPreferences = getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE) // using sharedPreferences in order to sign out later
        val isAuthenticated = sharedPreferences.getBoolean("isAuthenticated", false)

        if (isAuthenticated) {
            startMainActivity()
        } else {
            val signInButton: Button = findViewById(R.id.signInButton)
            signInButton.setOnClickListener {
                signIn(request)
            }
        }
    }

    private fun signIn(request: GetCredentialRequest) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val credentialResult = credentialManager.getCredential(
                    request = request,
                    context = this@AuthActivity
                )
                handleSignIn(credentialResult)
            } catch (e: Exception) {
                Log.e("AuthActivity", "Credential retrieval failed", e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                Log.d("AuthActivity", "Received PublicKeyCredential: $responseJson")
                // Handle PublicKeyCredential if necessary
            }
            is PasswordCredential -> {
                //val username = credential.id
                //val password = credential.password
                // Handle password credential
            }
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("AuthActivity", "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e("AuthActivity", "Unexpected type of credential")
                }
            }
            else -> {
                Log.e("AuthActivity", "Unsupported credential type")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        if (idToken != null) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("AuthActivity", "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("AuthActivity", "signInWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                }
        } else {
            Log.e("AuthActivity", "Google ID token is null")
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            setAuthenticated()
            startMainActivity()
        } else {
            Log.e("AuthActivity", "Authentication failed")
        }
    }

    private fun setAuthenticated() {
        val sharedPreferences = getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isAuthenticated", true)
        editor.apply()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

// USER NAME AND IMAGE RETRIEVAL FROM GOOGLE ACCOUNT AND OUTPUT (using Glide component, the following snippet is from another activity)
/*
        val usernameTextView = findViewById<TextView>(R.id.usernameTextView) // a textview to output user name
        val profileImageView = findViewById<ImageView>(R.id.profileImageView) // an imageview to show user pfp

        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser, usernameTextView, profileImageView)
        } else {
            // User not authenticated, redirect to AuthActivity
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser, usernameTextView: TextView, profileImageView: ImageView) {
        val displayName = user.displayName
        val photoUrl = user.photoUrl

        usernameTextView.text = if (displayName != null) "$displayName!"

        if (photoUrl != null) {
            Glide.with(this)
                .load(photoUrl)
                .error(R.drawable.ic_userphotodefault)
                .circleCrop()
                .into(profileImageView)

            val backgroundDrawable = profileImageView.background as GradientDrawable
            backgroundDrawable.cornerRadius =
                resources.getDimensionPixelSize(R.dimen.new_radius).toFloat()
            profileImageView.background = backgroundDrawable
        }
    }
*/

// USER SIGN-OUT
/*
        signOutButton.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isAuthenticated", false)
            editor.apply()
            clearCredentialState()
        }
*/
