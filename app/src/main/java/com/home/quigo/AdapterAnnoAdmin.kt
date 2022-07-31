package com.home.quigo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.home.quigo.databinding.RowAnnoAdminBinding

class AdapterAnnoAdmin: RecyclerView.Adapter<AdapterAnnoAdmin.HolderPostAdmin>, Filterable {

    //context
    private var context: Context

    //arrayList to hold posts
    public var annoArrayList:ArrayList<ModelAnno>
    private val filterList:ArrayList<ModelAnno>

    //filter object
    private var filter: FilterAnnoAdmin? = null

    //Constructor
    constructor(context: Context, annoArrayList: ArrayList<ModelAnno>) : super() {
        this.context = context
        this.annoArrayList = annoArrayList
        this.filterList = annoArrayList
    }


    //viewBinding
    private lateinit var binding:RowAnnoAdminBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPostAdmin {
        //bind/inflate layout row_post_admin.xml
        binding = RowAnnoAdminBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderPostAdmin(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPostAdmin, position: Int) {
        //*--------Get Data, Set Data, Handle click etc-------*/

        // get data
        val model= annoArrayList[position]
        val postId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val timestamp = model.timestamp

        //convert timestamp to dd//MM/yyyy format
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        //set data
        holder.titleTV.text = title
        holder.descriptionTV.text = description
        holder.dateTV.text = formattedDate

        //load category
        MyApplication.loadCategory(categoryId, holder.categoryTV)

        //handle click, show dialog with option 1) Edit Book, 2) Delete Book
        holder.moreBtn.setOnClickListener{
            moreOptionsDialog(model, holder)
        }

        //handle item click, open postDetailActivity
        holder.itemView.setOnClickListener{
            //intent with book id
            val intent = Intent(context, AnnoDetailActivity::class.java)
            intent.putExtra("postId", postId) //will be used to load book details
            context.startActivity(intent)
        }

    }

    private fun moreOptionsDialog(model: ModelAnno, holder: AdapterAnnoAdmin.HolderPostAdmin) {
      //get id, url, title of book
        val postId = model.id
        val postTitle = model.title

        //option to show in dialog
        val options = arrayOf("Edit", "Delete")

        //alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options){dialog, position->
                //handle item click
                if(position==0){
                    //Edit is clicked
                    val intent = Intent(context, AnnoEditActivity::class.java)
                    intent.putExtra("postId", postId)//passed bookId, will be used to edit the book
                    context.startActivity(intent)
                }
                else if(position ==1) {
                    //Delete is clicked

                    //Show confirmation message first
                    MyApplication.deletePost(context, postId, postTitle)
                }
            }
            .show()
    }

    override fun getItemCount(): Int {
        return annoArrayList.size //items count
    }

    override fun getFilter(): Filter {
      if(filter == null){
          filter = FilterAnnoAdmin(filterList, this)
      }
        return filter as FilterAnnoAdmin
    }

    /*View Holder class for row_anno_admin.xml*/
    inner class HolderPostAdmin(itemView: View): RecyclerView.ViewHolder(itemView){
        //UI Views of row_post_admin.xml
       // val progressBar = binding.progressBar
        val titleTV = binding.titleTv
        val descriptionTV = binding.descriptionTV
        val categoryTV = binding.categoryTV
        val dateTV = binding.dateTV
        val moreBtn = binding.moreBtn
    }
}