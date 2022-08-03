package com.home.quigo

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.home.quigo.databinding.ActivityAnnoEditBinding

class AnnoEditActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityAnnoEditBinding

    private companion object{
        private const val TAG = "IMG_EDIT_TAG"
    }

    //book id get from intent started from AdapterAnnoAdmin
    private var postId=""

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    //arraylist to hold category ids
    private lateinit var categoryIdArrayList: ArrayList<String>

    //arraylist to hold category titles
    private lateinit var categoryTitleArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get post id to edit the post infos
        postId= intent.getStringExtra("postId")!!

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        loadPostInfo()

        //handle click, goback
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        //handle click, pick category
        binding.categoryTV.setOnClickListener {
            categoryDialog()
        }

        //handle click, begin update
        binding.submitBtn.setOnClickListener {
            validateData()
        }

    }

    private fun loadPostInfo() {
        Log.d(TAG, "loadPostInfo: Loading post info")

        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.keepSynced(true)

        ref.child(postId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book info
                    selectedCategoryId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val contact = snapshot.child("contact").value.toString()

                    //set to views
                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)
                    binding.contactEt.setText(contact)

                    //load back category info using categoryId
                    Log.d(TAG, "onDataChange: Loading post category info")
                    val refPostCategory = FirebaseDatabase.getInstance().getReference("Categories")
                    ref.keepSynced(true)

                    refPostCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //get category
                                val category = snapshot.child("category").value
                                //set to textview
                                binding.categoryTV.text = category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private var title = ""
    private var description=""
    private var contact=""

    private fun validateData() {
        //get Data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        contact = binding.contactEt.text.toString().trim()

        //validate data
        if(title.isEmpty()){
            Toast.makeText(this, "Enter title", Toast.LENGTH_SHORT).show()
        }
        else if(description.isEmpty()){
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show()
        }
        else if(contact.isEmpty()){
            Toast.makeText(this, "Enter Price", Toast.LENGTH_SHORT).show()
        }
        else if(selectedCategoryId.isEmpty()){
            Toast.makeText(this, "Pick a Category", Toast.LENGTH_SHORT).show()
        }
        else{
            updatePost()
        }
    }

    private fun updatePost() {
        Log.d(TAG, "updatePost: Starting updating post info...")

        //show progress
        progressDialog.setMessage("Updating post info")
        progressDialog.show()

        //setup data to update to db, spellings key must be same as in firebase
        val hashMap = HashMap<String, Any>()
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["contact"] = "$contact"
        hashMap["categoryId"] = "$selectedCategoryId"

        //start updating
        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.keepSynced(true)

        ref.child(postId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updatePost: Updated successfully...")
                Toast.makeText(this, "Updated successfully...", Toast.LENGTH_SHORT).show()
                //clear fields
                binding.titleEt.setText("")
                binding.descriptionEt.setText("")
                binding.contactEt.setText("")
                binding.categoryTV.setText("Select Category")
            }
            .addOnFailureListener{e->
                Log.d(TAG, "updatePost: failed to Updated due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Updated due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun categoryDialog() {
        /*Show dialog to pick the category of post. we already got the categories*/

        //make String array from arraylist of string
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for(i in categoryTitleArrayList.indices){
            categoriesArray[i] = categoryTitleArrayList[i]
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){dialog, position ->
                //handle click, save clicked category id and title
                selectedCategoryId = categoryIdArrayList[position]
                selectedCategoryTitle = categoryTitleArrayList[position]

                //set to textview
                binding.categoryTV.text = selectedCategoryTitle
            }
            .show() //show dialog
    }

    private fun loadCategories() {
        Log.d(TAG, "loadCategories: loading categories...")

        categoryTitleArrayList = ArrayList()
        categoryIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.keepSynced(true)

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into them
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()

                for(ds in snapshot.children){
                    val id = "${ds.child("id").value}"
                    val category = "${ds.child("category").value}"

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG, "onDatachange: Category ID $id")
                    Log.d(TAG, "onDatachange: Category  $category")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}