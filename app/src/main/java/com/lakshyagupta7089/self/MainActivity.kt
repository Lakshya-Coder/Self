package com.lakshyagupta7089.self

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ActionMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.lakshyagupta7089.self.databinding.ActivityMainBinding
import com.lakshyagupta7089.self.util.JournalApi

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var currentUser: FirebaseUser? = null

    private val db = FirebaseFirestore.getInstance()
    private val collectionReference = db.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()
        authStateListener = FirebaseAuth.AuthStateListener { mAuth ->
            currentUser = mAuth.currentUser

            if (currentUser != null) {
                currentUser = mAuth.currentUser
                val currentUserId = currentUser?.uid

                collectionReference.whereEqualTo("userId", currentUserId)
                    .addSnapshotListener { querySnapshot, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }

                        val name = null
                        if (!querySnapshot!!.isEmpty) {
                            for (snapshot in querySnapshot) {
                                val journalApi = JournalApi.instance

                                journalApi?.setUserId(currentUserId!!)
                                journalApi?.setUsername(snapshot.getString("username")!!)

                                startActivity(Intent(this, JournalListActivity::class.java))
                                finish()
                            }
                        }
                    }
            }
        }

        binding.startButton.setOnClickListener {
            //we go to LoginActivity
            startActivity(
                Intent(applicationContext, LoginActivity::class.java)
            )
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        currentUser = firebaseAuth.currentUser
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()

        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

}