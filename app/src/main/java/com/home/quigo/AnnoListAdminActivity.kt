package com.home.quigo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.home.quigo.databinding.ActivityAnnoListAdminBinding

class AnnoListAdminActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityAnnoListAdminBinding

    private companion object{
        const val  TAG = "IMG_LIST_ADMIN_TAG"
    }

    //category id, title
    private var categoryId= ""
    private var category=""

    //arraylist to hold books
    private lateinit var annoArrayList:ArrayList<ModelAnno>

    //adapter
    private lateinit var adapterAnnoAdmin: AdapterAnnoAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnnoListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get from intent, that we passed from adapter
        val intent = intent
        categoryId= intent.getStringExtra("categoryId")!!
        category= intent.getStringExtra("category")!!

        //set post category
        binding.subTitleTv.text = category

        //load post/books
        loadPostList()

        //search
        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //filter data
                try {
                    adapterAnnoAdmin.filter!!.filter(s)
                }
                catch (e: Exception){
                    Log.d(TAG, "onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        //handle click, goback
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

    }

    private fun loadPostList() {
       //init arraylist
        annoArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Posts")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    annoArrayList.clear()
                    for(ds in snapshot.children){
                        //get data
                        val model = ds.getValue(ModelAnno::class.java)
                        //add to list
                        if (model != null) {
                            annoArrayList.add(model)
                            Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
                        }
                    }
                    //setup adapter
                    adapterAnnoAdmin = AdapterAnnoAdmin(this@AnnoListAdminActivity, annoArrayList)
                    binding.postsRV.adapter = adapterAnnoAdmin
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}