package com.home.quigo

object PostUtil {
    /*
   * Test Fails if ..
   * ....Title is empty
   * ....Description is empty
   * ....price is empty
   * */

    fun validatePostInput(
        title: String,
        description: String,
        price:String
    ):Boolean{
        if(title.isEmpty() || description.isEmpty() || price.isEmpty()){
            return false
        }
        return true
    }
}