package com.lakshyagupta7089.self

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.lakshyagupta7089.self.databinding.ActivityLoginBinding
import com.lakshyagupta7089.self.model.Journal
import com.lakshyagupta7089.self.util.JournalApi

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var currentUser: FirebaseUser? = null

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()

        binding.createAccountButton.setOnClickListener {
            startActivity(
                Intent(applicationContext, CreateAccountActivity::class.java)
            )
        }

        binding.emailSignInButton.setOnClickListener {
            loginEmailPasswordUser(
                binding.email.text.toString().trim(),
                binding.password.text.toString().trim()
            )
        }
    }

    private fun loginEmailPasswordUser(email: String, pwd: String) {
        binding.loginProgress.visibility = View.VISIBLE

        if (
            !TextUtils.isEmpty(email) &&
            !TextUtils.isEmpty(pwd)
        ) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = task.result?.user
                        val currentUserId = user?.uid.toString()

                        collectionReference.whereEqualTo("userId", currentUserId)
                            .addSnapshotListener { value, error ->
                                if (error != null) {
                                    return@addSnapshotListener
                                } else {
                                    if (!value!!.isEmpty) {
                                        binding.loginProgress.visibility = View.INVISIBLE

                                        for (snapshot in value) {
                                            val journalApi = JournalApi.instance

                                            journalApi?.setUsername(snapshot.getString("username")!!)
                                            journalApi?.setUserId(snapshot.getString("userId")!!)

                                            //Go to ListActivity
                                            startActivity(Intent(
                                                this,
                                                PostJournalActivity::class.java
                                            ))
                                        }
                                    }
                                }
                            }
                    }
                }
                .addOnFailureListener { task ->
                    binding.loginProgress.visibility = View.INVISIBLE
                }
        } else {
            binding.loginProgress.visibility = View.INVISIBLE
            Toast.makeText(
                this,
                "Please enter email and password",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}