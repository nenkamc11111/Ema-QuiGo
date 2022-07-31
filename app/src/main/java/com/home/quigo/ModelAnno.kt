package com.home.quigo

class ModelAnno {

    //Variables
    var uid:String =""
    var id:String =""
    var title:String =""
    var description:String =""
    var categoryId:String =""
    var timestamp:Long =0
    var viewscount:Long =0
    var contact:String =""

    //empty constructor (required by firebase)
    constructor()

    //parameterized constructor
    constructor(
        uid: String,
        id: String,
        title: String,
        description: String,
        categoryId: String,
        timestamp: Long,
        viewscount: Long,
        contact: String
    ) {
        this.uid = uid
        this.id = id
        this.title = title
        this.description = description
        this.categoryId = categoryId
        this.timestamp = timestamp
        this.viewscount = viewscount
        this.contact = contact
    }


}