package com.home.quigo

import android.widget.Filter

//Used to filter data from recyclerview | search post from post list in recyclerview
class FilterAnnoAdmin : Filter{
    //arraylist in which we want to search
    var filterList: ArrayList<ModelAnno>
    //adapter in which filter need to be implemented
    var adapterAnnoAdmin: AdapterAnnoAdmin

    //constructor
    constructor(filterList: ArrayList<ModelAnno>, adapterAnnoAdmin: AdapterAnnoAdmin) {
        this.filterList = filterList
        this.adapterAnnoAdmin = adapterAnnoAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint
        var results = FilterResults()
        //value to be searched should not be null and not empty
        if(constraint != null && constraint.isNotEmpty()){
            //change to upper case, or lowercase to avoid case sensitivity
            constraint = constraint.toString().lowercase()
            var filteredModels = ArrayList<ModelAnno>()
            for (i in filterList.indices){
                //validate if match
                if(filterList[i].title.lowercase().contains(constraint)) {
                    //searched value is similar to value in list, add filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //searched value is either null or empty, return all data
            results.count= filterList.size
            results.values = filterList

        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        //apply filter changes
        adapterAnnoAdmin.annoArrayList = results.values as ArrayList<ModelAnno>

        //notify changes
        adapterAnnoAdmin.notifyDataSetChanged()
    }


}