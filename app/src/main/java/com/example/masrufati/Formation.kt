package com.example.masrufati

interface Formation {
    fun formatString(string:String):String{
        throw Exception("You don't override this method : interface Formation line 5")
    }
}