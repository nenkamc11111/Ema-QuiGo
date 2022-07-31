package com.home.quigo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.home.quigo.databinding.ActivityAnnoViewBinding

class AnnoViewActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityAnnoViewBinding

    //TAG
    private companion object{
        const val TAG = "IMG_VIEW_TAG"
    }

    //book id
    var postId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get post id from intent,
        postId = intent.getStringExtra("postId")!!
        loadBookDetails()

        //handle click, goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get Post Url from DB")
        //Db reference to get book details

        //Step 1) Get Book Url Using Book Id
        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.child(postId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book url
                    val postUrl = snapshot.child("url").value
                    Log.d(TAG, "onDataChange: POST_URL: $postUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }


}