package com.example.masrufati

class Trip : Validation, Formation {
    private var id: Int = -1
    private var initialLocation: String
    private var finalLocation: String
    private var cost: Int
    private var date: String

    constructor(
        id: Int,
        initialLocation: String?,
        finalLocation: String?,
        cost: Int?,
        date: String
    ) {   // when get date from database
        this.id = checkId(id)
        this.initialLocation = checkString(initialLocation)
        this.finalLocation = checkString(finalLocation)
        this.cost = checkInt(cost)
        this.date = checkDate(date)
    }

    constructor(
        initialLocation: String?,
        finalLocation: String?,
        cost: Int?
    ) {       // new trip
        this.initialLocation = checkString(initialLocation)
        this.finalLocation = checkString(finalLocation)
        this.cost = checkInt(cost)
        this.date = Date.currentDate()
    }

    fun getId() = checkId(this.id)

    fun setId(id: Int) {
        this.id = checkId(id)
    }

    fun getInitialLocation() = this.initialLocation

    fun setInitialLocation(initialLocation: String?) {
        this.initialLocation = checkString(initialLocation)
    }

    fun getFinalLocation() = this.finalLocation

    fun setFinalLocation(finalLocation: String?) {
        this.finalLocation = checkString(finalLocation)
    }

    fun getCost() = this.cost

    fun setCost(cost: Int?) {
        this.cost = checkInt(cost)
    }

    fun getDate() = this.date

    override fun checkString(string: String?): String {
        return if (!(string.isNullOrEmpty() || string.isNullOrBlank() || string == "")) formatString(
            string
        )
        else throw NullPointerException("Empty String in class Trip line 44")
    }

    override fun checkInt(int: Int?): Int {
        return if (int != null && int > 0) int
        else throw NullPointerException("Invalid int in class Trip line 48")
    }

    override fun isListNull(list: MutableList<Note>?): MutableList<Note> {
        return list ?: throw NullPointerException("Empty List of Note in class Trip line 53")
    }

    override fun checkDate(date: String): String {
        if (date.length == 10) return date
        throw NullPointerException("Invalid date of Note in class Trip line 60")
    }

    override fun checkId(id: Int):Int {
        if(id > -1) return id
        throw NumberFormatException("Invalid id")
    }

    override fun formatString(string: String): String {
        var newString = ""
        for (i in string) {
            if (i == ' ' && newString != "" && newString[newString.length - 1] != ' ') newString += i
            else if (i != ' ') newString += i
        }
        return newString
    }

}