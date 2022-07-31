package com.home.quigo

object RegistrationUtil {
    private val existingUser = listOf("Cedric","David","Pascal")
    /*
    * Test Fails if ..
    * ....Username / Password is empty
    * ....Username is already taken
    * ....Confirmed password is not the same as real password
    * ....password is less than 6 chars
    * ....password contains no digit
    * */
    //specify the signature
    fun validateRegistrationInput(
        username: String,
        password: String,
        confirmPassword:String
    ):Boolean{
        if(username.isEmpty() || password.isEmpty()){
            return false
        }
        if(username in existingUser){
            return false
        }
        if(password != confirmPassword){
            return false
        }
        if(password.length < 6){
            return false
        }
        if(password.count{it.isDigit()}<2){
            return false
        }
        return true
    }
}