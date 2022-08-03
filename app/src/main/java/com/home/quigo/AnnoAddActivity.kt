package com.home.quigo

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.home.quigo.databinding.ActivityAnnoAddBinding

class AnnoAddActivity : AppCompatActivity() {

    //Setup view binding activity_post_add --> ActivityPostAddBinding
    private lateinit var binding: ActivityAnnoAddBinding

    //Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog (show while uploading post)
    private lateinit var progressDialog: ProgressDialog

    //arraylist to hold post categories
    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    //TAG
    private val TAG = "POST_ADD_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnoAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadPostCategories()

        //init progressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click, go back button
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        //handle click, show category pick dialog
        binding.categoryTV.setOnClickListener{
            categoryPickDialog()
        }

        //handle click, start uploading post
        binding.submitBtn.setOnClickListener{

         //STEP1: Validate Data
         //STEP2: Upload post to firebase storage
         //STEP3: Upload Post info to firebase db
          validateData()
        }

    }

    private var title= ""
    private var description=""
    private var category = ""
    private var contact =""

    private fun validateData() {
        //STEP1: Validate Data
        Log.d(TAG, "validateData: Validating data")

        //get data
        title       = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category    = binding.categoryTV.text.toString().trim()
        contact     = binding.contactEt.text.toString().trim()

        //validate
        if(title.isEmpty()){
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show()
        }
        else if(description.isEmpty()){
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show()
        }
        else if(category.isEmpty()){
            Toast.makeText(this, "Please Select a category...", Toast.LENGTH_SHORT).show()
        }
        else if(contact.isEmpty()){
            Toast.makeText(this, "Enter Contact...", Toast.LENGTH_SHORT).show()
        }
        else{
            //data validated, Upload post to firebase storage
            uploadPostToStorage()
        }
    }

    private fun uploadPostToStorage() {
        //STEP2: Upload post to firebase storage
        Log.d(TAG, "uploadPostToStorage: uploading to storage")

        //show progress dialog
        progressDialog.setMessage("Uploading POst..")
        progressDialog.show()

        //timestamp
        val timestamp = System.currentTimeMillis()

        //path of post in firebase storage
        val filePathAndName = "Posts/$timestamp"

        uploadPostInfoToDB( timestamp)

    }
    //save data in database
    private fun uploadPostInfoToDB( timestamp: Long) {
        //STEP4: Upload Post info to firebase db
        Log.d(TAG, "uploadPostInfoToDb: uploading to db")
        progressDialog.setMessage("Uploading post info..")

        //uid of current user
        val uid = firebaseAuth.uid

        //setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["contact"] = "$contact"

        //db reference DB > Posts > PostId > (Post Info)
        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.keepSynced(true)

        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPostInfoToDB: uploaded to db")
                progressDialog.dismiss()
                Toast.makeText(this, "data uploaded...", Toast.LENGTH_SHORT).show()

                //clear fields
                binding.titleEt.setText("")
                binding.descriptionEt.setText("")
                binding.contactEt.setText("")
                binding.categoryTV.setText("Select Category")

            }
            .addOnFailureListener{e->
                Log.d(TAG, "uploadPostInfoToDB: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload due to${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPostCategories() {
        Log.d(TAG, "loadPostCategories: Loading post categories")
        //init arrayList
        categoryArrayList = ArrayList()

        //DB reference to load categories DF > Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.keepSynced(true)

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //Clear List before adding data
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    //add to arraylist
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private var selectedCategoryId=""
    private var selectedCategoryTitle=""

    private fun categoryPickDialog(){
        Log.d(TAG, "categoryPickDialog: Showing post category pick dialog")

        //get string array of categories from arraylist
        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for(i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){dialog, which->
                  //handle item click
                  //get clicked item
                  selectedCategoryTitle = categoryArrayList[which].category
                  selectedCategoryId = categoryArrayList[which].id

                   //set category to textview
                  binding.categoryTV.text = selectedCategoryTitle

                  Log.d(TAG, "categoryPickDialog: Selected Category ID:$selectedCategoryId")
                  Log.d(TAG, "categoryPickDialog: Selected Category TITLE:$selectedCategoryTitle")
            }
            .show()
    }

}