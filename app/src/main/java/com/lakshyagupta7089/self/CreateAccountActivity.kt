package com.lakshyagupta7089.self

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.lakshyagupta7089.self.databinding.ActivityCreateAccountBinding
import com.lakshyagupta7089.self.util.JournalApi

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var currentUser: FirebaseUser? = null

    //Firestore connection
    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()

        authStateListener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser

            if (currentUser != null) {
                //user is  already login...
            } else {
                //no user yet
            }
        }

        binding.createAccountButton.setOnClickListener {
            val email = binding.emailAccount.text.toString()
            val password = binding.passwordAccount.text.toString()
            val username = binding.usernameAccount.text.toString()

            if (!TextUtils.isEmpty(email) &&
                !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(username)) {

                createUserEmailAccount(
                    email.trim(),
                    password.trim(),
                    username.trim()
                )
            } else {
                makeToast("Empty Fields Not Allowed", Toast.LENGTH_LONG)
            }
        }
    }

    private fun createUserEmailAccount(
        email: String,
        password: String,
        username: String
    ) {
        if (!TextUtils.isEmpty(email) &&
            !TextUtils.isEmpty(password) &&
            !TextUtils.isEmpty(username)) {

            binding.createAcctProgress.visibility = View.VISIBLE

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //we take user to AddJournalActivity
                        currentUser = firebaseAuth.currentUser

                        val currentUserId = currentUser?.uid.toString()

                        //create a user Map so we can create a user in the User collection
                        val userObject = HashMap<String, String>()
                        userObject["userId"] = currentUserId
                        userObject["username"] = username

                        //save to our firestore database
                        collectionReference.add(userObject)
                            .addOnSuccessListener { documentRef ->
                                documentRef.get()
                                    .addOnCompleteListener { task ->
                                        if (task.result!!.exists()) {
                                            binding.createAcctProgress.visibility = View.GONE

                                            val name = task.result!!
                                                .getString("username")

                                            val journalApi = JournalApi.instance //Global API
                                            journalApi?.setUserId(currentUserId)
                                            journalApi?.setUsername(name!!)

                                            val intent = Intent(
                                                applicationContext,
                                                PostJournalActivity::class.java
                                            )
//                                            intent.putExtra("username", name)
//                                            intent.putExtra("userId", currentUserId)

                                            startActivity(
                                                intent
                                            )
                                        } else {
                                            binding.createAcctProgress.visibility = View.GONE
                                        }
                                    }
                            }
                            .addOnFailureListener {

                            }

                    } else {
                        //something went wrong
                    }
                }
                .addOnFailureListener {}

        } else {
            //toast msg => "Pleas fill the following first!"
        }
    }

    override fun onStart() {
        super.onStart()

        currentUser = firebaseAuth.currentUser
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    private fun makeToast(msg: String, length: Int) {
        Toast.makeText(
            applicationContext,
            msg,
            length
        ).show()
    }
}