package com.home.quigo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.home.quigo.databinding.RowCategoryUserBinding

class AdapterCategoryUser: RecyclerView.Adapter<AdapterCategoryUser.HolderCategory>,Filterable {

    private val context: Context
    public var categoryArrayList: ArrayList<ModelCategory>
    private var filterList:ArrayList<ModelCategory>

    private var filter:FilterCategoryUser? = null

    private lateinit var binding: RowCategoryUserBinding

    //Constructor
    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
       //inflate bind row_category_user.xml
        binding = RowCategoryUserBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
       /*----- Get Data, Set Data, Handle clicks etc -----*/

        //Get Data
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        //Set Data
        holder.categoryTV.text = category



        //handle click, start post list user activity, also passe post id, title
        holder.itemView.setOnClickListener{
            val intent = Intent(context, AnnoListAdminActivity::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }

    }



    override fun getItemCount(): Int {
        //number of items in the list
        return categoryArrayList.size
    }

    //ViewHolder class to hold/init UI views for row_category.xml
    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){

        //init ui views
        var categoryTV: TextView = binding.categoryTV

    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterCategoryUser(filterList, this)
        }
        return  filter as FilterCategoryUser
    }

}