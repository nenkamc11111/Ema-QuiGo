package com.home.quigo

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.HashMap

class MyApplication: Application() {

    //An application class that will contain the functions that will be used multiple places in app
    override fun onCreate() {
        super.onCreate()
    }

    companion object{
       //create a static method to convert timestamp to proper date format
        fun formatTimeStamp(timestamp: Long): String{
           val cal = Calendar.getInstance(Locale.ENGLISH)
           cal.timeInMillis = timestamp
           //format dd/MM/yyyy
           return DateFormat.format("dd/MM/yyyy", cal).toString()
        }


        fun loadCategory(categoryId: String, categoryTv: TextView){
            //load category using category id from firebase
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //get category
                        val category = "${snapshot.child("category").value}"

                        //set category
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }

        fun deletePost(context: Context, postId: String, postTitle: String){
            //param details
            //1) context, used when required e.g for progressdialog, toast
            //2) postId, to delete post from db
            //3) postUrl, delete post from firebase storage
            //4) postTitle, show in dialog etc

            val TAG = "DELETE_POST_TAG"

            Log.d(TAG, "deletePost: deleting...")

            //progress dialog
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Deleting $postTitle...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Log.d(TAG, "deleteBook: Deleting from storage...")
            val ref = FirebaseDatabase.getInstance().getReference("Posts")
            ref.child(postId)
                .removeValue()
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Successfully deleted...", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "deletepost: data Deleted from db..")
                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    Log.d(TAG, "deletepost: FAiled to delete from DB due to ${e.message}")
                    Toast.makeText(context, "Failed to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        fun incrementBookViewCount(postId: String){
            //1) Get current book views count
            val ref = FirebaseDatabase.getInstance().getReference("Posts")
            ref.child(postId)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // get views count
                        var viewsCount = "${snapshot.child("viewsCount").value}"

                        if(viewsCount == "" || viewsCount == "null"){
                            viewsCount="0";
                        }

                        //2 Increment views count
                        val newViewsCount = viewsCount.toLong() + 1

                        //setup data to update in db
                        val hashMap = HashMap<String, Any>()
                        hashMap["viewsCount"] = newViewsCount

                        //set to db
                        val dbRef = FirebaseDatabase.getInstance().getReference("Posts")
                        dbRef.child(postId)
                            .updateChildren(hashMap)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }


}