package com.home.quigo

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.home.quigo.databinding.ActivityCategoryAddBinding
import com.home.quigo.databinding.ActivityDashboardBinding

class CategoryAddActivity : AppCompatActivity() {

    //view binding
    private lateinit var  binding: ActivityCategoryAddBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //configure progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, go back
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        //handle click, begin upload category
        binding.submitBtn.setOnClickListener{
            validateData()
        }
    }

    private var category = ""

    private fun validateData() {
        //validate data

        //get data
        category = binding.categoryEt.text.toString().trim()

        //validate data
        if(category.isEmpty()){
            Toast.makeText(this, "Enter Category Name...", Toast.LENGTH_SHORT).show()
        }
        else{
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        //show Progress
        progressDialog.show()

        //get timestamp
        val timestamp = System.currentTimeMillis()

        //setup data to add in firebase db
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${firebaseAuth.uid}"

        //add to firebase db: Database Root > Categories > categoryId > category info
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.keepSynced(true)

        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                // added successfully
                progressDialog.dismiss()
                Toast.makeText(this, "Category Created Successfully...", Toast.LENGTH_SHORT).show()
                binding.categoryEt.setText("")
            }
            .addOnFailureListener{e->
                //failed to add
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}