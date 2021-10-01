package com.lakshyagupta7089.self

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lakshyagupta7089.self.databinding.ActivityPostJournalBinding
import com.lakshyagupta7089.self.model.Journal
import com.lakshyagupta7089.self.util.JournalApi
import java.util.*

class PostJournalActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val TAG = "PostJournalActivity"
    }

    private var imageUri: Uri? = null
    private lateinit var binding: ActivityPostJournalBinding

    private lateinit var currentUserName: String
    private lateinit var currentUserId: String

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private var user: FirebaseUser? = null

    //connection to firestore
    private val db = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    private val collectionReference = db.collection("Journal")


    private val startActivityToResult =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data

                if (data != null) {
                    imageUri = data.data

                    binding.postImageView.setImageURI(imageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostJournalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.elevation = 0F

        firebaseAuth = FirebaseAuth.getInstance()

        binding.postSaveJournalButton.setOnClickListener(this)
        binding.postCameraButton.setOnClickListener(this)

        binding.postProgressBar.visibility = View.INVISIBLE

        if (JournalApi.instance != null) {
            currentUserId = JournalApi.instance!!.getUserId().toString()
            currentUserName = JournalApi.instance!!.getUsername().toString()

            binding.postUserNameTextview.text = currentUserName
        }

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            user = firebaseAuth.currentUser

            if (user != null) {

            } else {

            }
        }

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.postSaveJournalButton.id -> {
                //save Journal
                saveJournal()
            }
            binding.postCameraButton.id -> {
                //get image from gallery/phone
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"

                startActivityToResult.launch(intent)
            }
        }
    }

    private fun saveJournal() {
        val title = binding.postTitleEt.text.toString().trim()
        val thoughts = binding.postDescriptionEt.text.toString().trim()

        binding.postProgressBar.visibility = View.VISIBLE

        if (!TextUtils.isEmpty(title) &&
            !TextUtils.isEmpty(thoughts) &&
            imageUri != null
        ) {

            val filepath = storageReference //.../journal_images/our_image.jpeg
                .child("journal_image")
                .child("my_image_${Timestamp.now().seconds}")
            filepath.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    filepath.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()

                        // Todo: create a Journal Object - model
                        val journal = Journal(
                            title = title,
                            thoughts = thoughts,
                            imageUrl = imageUrl,
                            timeAdded = Timestamp(Date()),
                            userName = currentUserName,
                            userId = currentUserId
                        )


                        // Todo: invoke our collectionReference
                        collectionReference.add(journal)
                            .addOnSuccessListener {
                                binding.postProgressBar.visibility = View.INVISIBLE

                                startActivity(Intent(this, JournalListActivity::class.java))
                                finish()
                            }
                            .addOnSuccessListener {
                                binding.postProgressBar.visibility = View.INVISIBLE
                            }

                        // Todo: and save a Journal instance.
                    }

                }
                .addOnFailureListener {
                    binding.postProgressBar.visibility = View.INVISIBLE

                    Log.d(TAG, "saveJournal: ${it.message}")
                }

        } else {
            binding.postProgressBar.visibility = View.INVISIBLE
        }
    }

    override fun onStart() {
        super.onStart()

        user = firebaseAuth.currentUser
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()

        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}