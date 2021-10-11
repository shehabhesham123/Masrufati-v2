package com.example.masrufati

interface Validation {
    fun checkString(string: String?):String{
        throw Exception("You don't override this method : in interface Validation line 5")
    }

    fun isListNull(list: MutableList<Note>?):MutableList<Note>{
        throw Exception("You don't override this method : in interface Validation line 9")
    }

    fun checkInt(int:Int?):Int{
        throw Exception("You don't override this method : in interface Validation line 13")
    }

    fun checkDate(date:String):String{
        throw Exception("You don't override this method : in interface Validation line 17")
    }

    fun checkId(id:Int):Int{
        throw Exception("You don't override this method : in interface Validation line 21")
    }
}