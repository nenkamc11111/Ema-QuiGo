package com.home.quigo

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.home.quigo.databinding.ActivityAnnoDetailBinding

class AnnoDetailActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityAnnoDetailBinding

    private companion object{
        //TAG
        const val TAG = "POST_DETAILS_TAG"
    }

    //book id, get from intent
    private var postId =""

    //get from firebase
    private var postTitle =""

    //share button
    private lateinit var Share: Button;

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnoDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get Post id from intent
        postId = intent.getStringExtra("postId")!!

        //init progress bar
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //increment book view count, whenever this page starts
        MyApplication.incrementBookViewCount(postId)

        loadPostDetails()

        //handle backbutton click, goback
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, share post
        binding.sharebtn.setOnClickListener {
            val shareBody = "Comming Soon on playStore: https://play.google.com/store/apps?gl=DE"
            val shareSub = postTitle

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"

            shareIntent.putExtra(Intent.EXTRA_SUBJECT,shareSub)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,shareBody)

            startActivity(Intent.createChooser(shareIntent, "Share To:"))



        }

    }



    private fun loadPostDetails() {
        //Posts > postId > Details
        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.keepSynced(true)

        ref.child(postId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get data
                    val categoryId = "${snapshot.child("categoryId").value}"
                    val description = "${snapshot.child("description").value}"
                    val contact = "${snapshot.child("contact").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    postTitle = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"

                    //format date
                    val date=MyApplication.formatTimeStamp(timestamp.toLong())

                    //load post category
                    MyApplication.loadCategory(categoryId, binding.categoryTv)

                    //set data
                    binding.titleTv.text = postTitle
                    binding.descriptionTv.text = description
                    binding.contactTv.text = contact
                    binding.viewsTv.text= viewsCount
                    binding.dateTv.text = date

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }
}