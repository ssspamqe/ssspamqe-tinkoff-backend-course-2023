package edu.hw5

class Task4 {

    public fun validatePassword(password:String):Boolean{
        return password.matches(".*[~!@#\$%^&*|].*".toRegex())
    }

}
