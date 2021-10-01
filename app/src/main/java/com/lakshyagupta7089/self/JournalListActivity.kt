package com.lakshyagupta7089.self

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.lakshyagupta7089.self.ui.JournalRecyclerViewAdapter
import com.lakshyagupta7089.self.databinding.ActivityJournalListBinding
import com.lakshyagupta7089.self.model.Journal
import com.lakshyagupta7089.self.util.JournalApi

class JournalListActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "JournalListActivity"
    }

    private lateinit var binding: ActivityJournalListBinding

    private var firebaseAuth: FirebaseAuth? = null
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var user: FirebaseUser? = null
    private lateinit var adapter: JournalRecyclerViewAdapter

    private val db = FirebaseFirestore.getInstance()
    private lateinit var storageReference: StorageReference

    private lateinit var journalList: ArrayList<Journal>

    private val collectionReference = db.collection("Journal")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJournalListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth?.currentUser

        journalList = ArrayList()

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                if (user != null && firebaseAuth != null) {
//                    db.collection("Users").whereEqualTo("userId", user!!.uid)
//                        .addSnapshotListener { value, error ->
//                            if (error != null) {
//                                return@addSnapshotListener
//                            } else {
//                                if (!value!!.isEmpty) {
//                                    for (snapshot in value) {
//                                        val journalApi = JournalApi.instance
//
//                                        journalApi?.setUsername(snapshot.getString("username")!!)
//                                        journalApi?.setUserId(snapshot.getString("userId")!!)
//
//                                        //Go to ListActivity
//                                        startActivity(Intent(
//                                            this,
//                                            PostJournalActivity::class.java
//                                        ))
//                                    }
//                                }
//                            }
//                        }
                    startActivity(
                        Intent(this, PostJournalActivity::class.java)
                    )
                    // finish()
                }
            }
            R.id.action_signout -> {
                if (user != null && firebaseAuth != null) {
                    firebaseAuth?.signOut()

                    startActivity(
                        Intent(this, MainActivity::class.java)
                    )
                    // finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        collectionReference.whereEqualTo("userId", JournalApi.instance?.getUserId())
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (snapshot in it) {
                        val journal = snapshot.toObject(Journal::class.java)
                        journalList.add(journal)
                    }

                    //Invoke recycler view
                    adapter = JournalRecyclerViewAdapter(this, journalList)
                    binding.recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                } else {
                    binding.listNoThoughts.visibility = View.VISIBLE
                }
            }
//            .addSnapshotListener { value, error ->
//                if (error != null) {
//                    return@addSnapshotListener
//                } else {
//                    if (!value!!.isEmpty) {
//                        for (snapshot in value) {
//                            journalList.add(snapshot.toObject(Journal::class.java))
//                            adapter.notifyDataSetChanged()
//                        }
//                    }
//                }
//            }
    }
}
