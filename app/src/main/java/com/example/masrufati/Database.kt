package com.example.masrufati

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlin.math.ceil


private const val DATABASE_NAME = "masrufati_database"
private const val version: Int = 3
private const val TRIP_TABLE_NAME = "trip_table"
private const val TRIP_ID_COLUMN_NAME_INT = "trip_id"
private const val TRIP_DATE_COLUMN_NAME_STRING = "trip_date"
private const val TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING = "trip_initial_Location"
private const val TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING = "trip_final_location"
private const val TRIP_COST_COLUMN_NAME_INT = "trip_distance"
private const val NOTE_TABLE_NAME = "note"
private const val NOTE_TABLE_NAME2 = "note2"
private const val NOTE_ID_COLUMN_NAME_INT = "id"
private const val NOTE_BACKGROUND_COLUMN_NAME_INT = "background"
private const val NOTE_TITLE_COLUMN_NAME_STRING = "title"
private const val NOTE_BODY_COLUMN_NAME_STRING = "body"


private const val JOURNEY_TABLE_NAME = "journey_table"
private const val JOURNEY_ID_COLUMN_NAME_INT = "journey_id"
private const val JOURNEY_START_DATE_COLUMN_NAME_STRING = "journey_start_date"
private const val JOURNEY_END_DATE_COLUMN_NAME_STRING = "journey_end_date"
private const val JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING = "journey_initial_address"
private const val JOURNEY_BANK_NAME_COLUMN_NAME_STRING = "journey_bank_name"
private const val JOURNEY_BANK_CITY_COLUMN_NAME_STRING = "journey_bank_city"
private const val JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING = "journey_bank_address"

private const val JOURNEY_NOTE_TABLE_NAME = "note"
private const val JOURNEY_NOTE_ID_COLUMN_NAME_INT = "id"
private const val JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT = "background"
private const val JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING = "title"
private const val JOURNEY_NOTE_BODY_COLUMN_NAME_STRING = "body"
private const val JOURNEY_NOTE_JOURNEY_ID_COLUMN_NAME_INT = "journey_id"

private const val TRANSPORTATION_TABLE_NAME = "transportation_table"
private const val TRANSPORTATION_ID_COLUMN_NAME_INT = "transportation_id"
private const val TRANSPORTATION_NAME_COLUMN_NAME_INT = "transportation_name"
private const val TRANSPORTATION_COST_COLUMN_NAME_FLOAT = "transportation_cost"
private const val TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT = "transportation_journey_id"

class OldTrip(
    val id: Int, val initialAddress: String, val finalAddress: String, val date: String,
    var cost: Float
)

abstract class Database(mContext: Context) :
    SQLiteOpenHelper(mContext, DATABASE_NAME, null, version) {
    override fun onCreate(p0: SQLiteDatabase?) {
        checkUpdate(p0!!, 0)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        checkUpdate(p0!!, p1)
    }

    private fun checkUpdate(sqLiteDatabase: SQLiteDatabase, lastVersion: Int) {
        if (lastVersion < 1) {
            Log.i("asd", "1")
            sqLiteDatabase.execSQL(
                "create table $JOURNEY_TABLE_NAME (" +
                        "$JOURNEY_ID_COLUMN_NAME_INT integer primary key AUTOINCREMENT not null," +
                        "$JOURNEY_START_DATE_COLUMN_NAME_STRING  text not null," +
                        "$JOURNEY_END_DATE_COLUMN_NAME_STRING  text null," +
                        "$JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING  text not null," +
                        "$JOURNEY_BANK_NAME_COLUMN_NAME_STRING  text not null," +
                        "$JOURNEY_BANK_CITY_COLUMN_NAME_STRING text not null," +
                        "$JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING text)"
            )

            sqLiteDatabase.execSQL(
                "create table $TRANSPORTATION_TABLE_NAME (" +
                        "$TRANSPORTATION_ID_COLUMN_NAME_INT integer primary key AUTOINCREMENT not null," +
                        "$TRANSPORTATION_NAME_COLUMN_NAME_INT integer not null," +
                        "$TRANSPORTATION_COST_COLUMN_NAME_FLOAT real not null," +
                        "$TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT integer not null," +
                        "FOREIGN KEY($TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT) REFERENCES $JOURNEY_TABLE_NAME($JOURNEY_ID_COLUMN_NAME_INT))"
            )
        }

        if (lastVersion < 2) {
            Log.i("asd", "2")
            sqLiteDatabase.execSQL(
                "create table $JOURNEY_NOTE_TABLE_NAME (" +
                        "$JOURNEY_NOTE_ID_COLUMN_NAME_INT integer primary key AUTOINCREMENT not null ," +
                        "$JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING text null ," +
                        "$JOURNEY_NOTE_BODY_COLUMN_NAME_STRING text null ," +
                        "$JOURNEY_NOTE_JOURNEY_ID_COLUMN_NAME_INT integer ," +
                        "$JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT integer, " +
                        "FOREIGN KEY($JOURNEY_NOTE_JOURNEY_ID_COLUMN_NAME_INT) REFERENCES $JOURNEY_TABLE_NAME($JOURNEY_ID_COLUMN_NAME_INT))"
            )
        }
        if (lastVersion < 3) {
            Log.i("asd", "3")
            sqLiteDatabase.execSQL(
                "create table $TRIP_TABLE_NAME (" +
                        "$TRIP_ID_COLUMN_NAME_INT integer primary key AUTOINCREMENT not null," +
                        "$TRIP_DATE_COLUMN_NAME_STRING  text not null," +
                        "$TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING  text not null," +
                        "$TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING  text not null," +
                        "$TRIP_COST_COLUMN_NAME_INT integer)"
            )

            sqLiteDatabase.execSQL(
                "create table $NOTE_TABLE_NAME2 (" +
                        "$NOTE_ID_COLUMN_NAME_INT integer primary key AUTOINCREMENT not null ," +
                        "$NOTE_TITLE_COLUMN_NAME_STRING text null ," +
                        "$NOTE_BODY_COLUMN_NAME_STRING text null ," +
                        "$NOTE_BACKGROUND_COLUMN_NAME_INT integer )"
            )

            getOldTrip(sqLiteDatabase)

            getOldNote(sqLiteDatabase)

        }

    }

    private fun reverseDate(date: String): String {
        var reverseDate: String = date.reversed()
        reverseDate = reverseDate.replaceRange(0, 2, "${reverseDate[1]}${reverseDate[0]}")
        reverseDate = reverseDate.replaceRange(3, 5, "${reverseDate[4]}${reverseDate[3]}")
        reverseDate = reverseDate.replaceRange(6, 10, "${reverseDate[9]}${reverseDate[8]}${reverseDate[7]}${reverseDate[6]}")
        return reverseDate
    }

    private fun getOldTrip(sqLiteDatabase: SQLiteDatabase) {
        var query =
            "select $JOURNEY_ID_COLUMN_NAME_INT, $JOURNEY_START_DATE_COLUMN_NAME_STRING," +
                    " $JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING, $JOURNEY_BANK_NAME_COLUMN_NAME_STRING," +
                    " $JOURNEY_BANK_CITY_COLUMN_NAME_STRING, $JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING from $JOURNEY_TABLE_NAME"
        val cursor = sqLiteDatabase.rawQuery(query, null)
        val oldTrip = mutableListOf<OldTrip>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(JOURNEY_ID_COLUMN_NAME_INT))
            val date = reverseDate(cursor.getString(
                cursor.getColumnIndex(
                    JOURNEY_START_DATE_COLUMN_NAME_STRING
                )
            ))
            val initialAddress = cursor.getString(
                cursor.getColumnIndex(
                    JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING
                )
            )
            val bankName = cursor.getString(
                cursor.getColumnIndex(
                    JOURNEY_BANK_NAME_COLUMN_NAME_STRING
                )
            )
            val bankCity = cursor.getString(
                cursor.getColumnIndex(
                    JOURNEY_BANK_CITY_COLUMN_NAME_STRING
                )
            )
            val bankAddress = cursor.getString(
                cursor.getColumnIndex(
                    JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING
                )
            )
            oldTrip.add(
                OldTrip(
                    id,
                    initialAddress,
                    if (bankAddress.isNullOrEmpty()) "$bankName-$bankCity" else "$bankName-$bankCity-$bankAddress",
                    date,
                    0f
                )
            )
        }

        for (i in oldTrip) {
            query =
                "select sum($TRANSPORTATION_COST_COLUMN_NAME_FLOAT) from $TRANSPORTATION_TABLE_NAME where " +
                        "$TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT = ? "
            val cursor2 = sqLiteDatabase.rawQuery(query, arrayOf(i.id.toString()))
            if (cursor2.moveToFirst()) {
                val cost =
                    cursor2.getFloat(cursor2.getColumnIndex("sum($TRANSPORTATION_COST_COLUMN_NAME_FLOAT)"))
                i.cost = ceil(cost)
            }

            val contentValues = ContentValues()
            contentValues.put(TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING, i.initialAddress)
            contentValues.put(TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING, i.finalAddress)
            contentValues.put(TRIP_DATE_COLUMN_NAME_STRING, i.date)
            contentValues.put(TRIP_COST_COLUMN_NAME_INT, i.cost)

            sqLiteDatabase.insert(TRIP_TABLE_NAME, null, contentValues)
        }
    }

    private fun getOldNote(sqLiteDatabase: SQLiteDatabase) {
        val query =
            "select $JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT, $JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING, " +
                    "$JOURNEY_NOTE_BODY_COLUMN_NAME_STRING from $JOURNEY_NOTE_TABLE_NAME"
        val cursor = sqLiteDatabase.rawQuery(query, null)
        while (cursor.moveToNext()) {

            Log.i("asd", "0")
            val title =
                cursor.getString(cursor.getColumnIndex(JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING))
            val body = cursor.getString(cursor.getColumnIndex(JOURNEY_NOTE_BODY_COLUMN_NAME_STRING))
            val background =
                cursor.getInt(cursor.getColumnIndex(JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT))

            val contentValues = ContentValues()
            contentValues.put(NOTE_TITLE_COLUMN_NAME_STRING, title)
            contentValues.put(NOTE_BODY_COLUMN_NAME_STRING, body)
            contentValues.put(NOTE_BACKGROUND_COLUMN_NAME_INT, background)
            sqLiteDatabase.insert(NOTE_TABLE_NAME2, null, contentValues)
        }

        sqLiteDatabase.execSQL("DROP TABLE $JOURNEY_TABLE_NAME")
        sqLiteDatabase.execSQL("DROP TABLE $JOURNEY_NOTE_TABLE_NAME;")
        sqLiteDatabase.execSQL("DROP TABLE $TRANSPORTATION_TABLE_NAME;")
        sqLiteDatabase.execSQL("ALTER TABLE $NOTE_TABLE_NAME2 RENAME TO $NOTE_TABLE_NAME")
    }
}

class AccessDatabase private constructor(mContext: Context) : Database(mContext) {

    private lateinit var sqLiteDatabase: SQLiteDatabase

    private fun openDB() {
        sqLiteDatabase = writableDatabase
    }

    private fun closeDB() {
        sqLiteDatabase.close()
    }

    fun newTrip(tripClass: Trip): Int {
        val contentValues = ContentValues()
        contentValues.put(TRIP_DATE_COLUMN_NAME_STRING, tripClass.getDate())
        contentValues.put(TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING, tripClass.getInitialLocation())
        contentValues.put(TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING, tripClass.getFinalLocation())
        contentValues.put(TRIP_COST_COLUMN_NAME_INT, tripClass.getCost())
        openDB()
        val id = sqLiteDatabase.insert(TRIP_TABLE_NAME, null, contentValues)
        closeDB()
        return id.toInt()
    }

    fun updateTrip(tripClass: Trip): Boolean {
        openDB()
        val contentValues = ContentValues()
        contentValues.put(TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING, tripClass.getInitialLocation())
        contentValues.put(TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING, tripClass.getFinalLocation())
        contentValues.put(TRIP_COST_COLUMN_NAME_INT, tripClass.getCost())
        val flag = sqLiteDatabase.update(
            TRIP_TABLE_NAME,
            contentValues,
            "$TRIP_ID_COLUMN_NAME_INT = ?",
            arrayOf(tripClass.getId().toString())
        )
        closeDB()
        return flag != 0
    }

    fun removeTrip(TripId: Int): Boolean {
        openDB()
        val flag = sqLiteDatabase.delete(
            TRIP_TABLE_NAME,
            "$TRIP_ID_COLUMN_NAME_INT = ?",
            arrayOf(TripId.toString())
        )
        closeDB()
        return flag != 0
    }

    @SuppressLint("Recycle")
    fun getTrip(TripId: Int): Trip {
        openDB()
        var tripClass: Trip? = null
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $TRIP_TABLE_NAME where $TRIP_ID_COLUMN_NAME_INT = ?",
            arrayOf(TripId.toString())
        )
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(TRIP_ID_COLUMN_NAME_INT))
            val date = cursor.getString(cursor.getColumnIndex(TRIP_DATE_COLUMN_NAME_STRING))
            val initialLocation = cursor.getString(
                cursor.getColumnIndex(
                    TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING
                )
            )
            val finalLocation =
                cursor.getString(cursor.getColumnIndex(TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING))
            val distance = cursor.getInt(cursor.getColumnIndex(TRIP_COST_COLUMN_NAME_INT))
            closeDB()
            tripClass = Trip(id, initialLocation, finalLocation, distance, date)
        }
        return tripClass!!
    }

    fun getLastTrip(): Trip {

        openDB()
        var tripClass: Trip? = null
        val cursor = sqLiteDatabase.rawQuery(
            "SELECT * FROM $TRIP_TABLE_NAME ORDER BY $TRIP_ID_COLUMN_NAME_INT DESC LIMIT 1;",
            arrayOf()
        )
        if (cursor.moveToFirst()) {
            val initialLocation =
                cursor.getString(cursor.getColumnIndex(TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING))
            val finalLocation =
                cursor.getString(cursor.getColumnIndex(TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING))
            val distance = cursor.getInt(cursor.getColumnIndex(TRIP_COST_COLUMN_NAME_INT))
            closeDB()
            tripClass = Trip(finalLocation, initialLocation, distance)     // reverse the trip
        }
        return tripClass!!
    }

    @SuppressLint("Recycle")
    fun getAllTrip(): MutableList<Trip> {
        openDB()
        val cursor = sqLiteDatabase.rawQuery("select * from $TRIP_TABLE_NAME", arrayOf())
        val listOfTrip = mutableListOf<Trip>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(TRIP_ID_COLUMN_NAME_INT))
            val date = cursor.getString(cursor.getColumnIndex(TRIP_DATE_COLUMN_NAME_STRING))
            val initialLocation = cursor.getString(
                cursor.getColumnIndex(
                    TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING
                )
            )
            val finalLocation =
                cursor.getString(cursor.getColumnIndex(TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING))
            val distance = cursor.getInt(cursor.getColumnIndex(TRIP_COST_COLUMN_NAME_INT))
            listOfTrip.add(
                Trip(id, initialLocation, finalLocation, distance, date)
            )

            closeDB()
        }
        return listOfTrip
    }

    @SuppressLint("Recycle")
    fun getAllTrip(date: String): MutableList<Trip> {
        openDB()
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $TRIP_TABLE_NAME where $TRIP_DATE_COLUMN_NAME_STRING = ? ",
            arrayOf(date)
        )
        val listOfJourney = mutableListOf<Trip>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(TRIP_ID_COLUMN_NAME_INT))
            val initialLocation = cursor.getString(
                cursor.getColumnIndex(
                    TRIP_INITIAL_ADDRESS_COLUMN_NAME_STRING
                )
            )
            val finalLocation =
                cursor.getString(cursor.getColumnIndex(TRIP_FINAL_ADDRESS_COLUMN_NAME_STRING))
            val distance = cursor.getInt(cursor.getColumnIndex(TRIP_COST_COLUMN_NAME_INT))
            listOfJourney.add(
                Trip(id, initialLocation, finalLocation, distance, date)
            )
        }
        closeDB()
        return listOfJourney
    }

    @SuppressLint("Recycle")

    fun newNote(note: Note): Int {
        val contentValues = ContentValues()
        contentValues.put(NOTE_TITLE_COLUMN_NAME_STRING, note.getTitle())
        contentValues.put(NOTE_BODY_COLUMN_NAME_STRING, note.getBody())
        contentValues.put(
            NOTE_BACKGROUND_COLUMN_NAME_INT,
            Note.Background.fromBackgroundClassToInt(note.getBackground())
        )
        openDB()
        val id = sqLiteDatabase.insert(NOTE_TABLE_NAME, null, contentValues)
        closeDB()
        return id.toInt()
    }

    fun updateNote(note: Note): Boolean {
        val contentValues = ContentValues()
        contentValues.put(NOTE_TITLE_COLUMN_NAME_STRING, note.getTitle())
        contentValues.put(NOTE_BODY_COLUMN_NAME_STRING, note.getBody())
        contentValues.put(
            NOTE_BACKGROUND_COLUMN_NAME_INT,
            Note.Background.fromBackgroundClassToInt(note.getBackground())
        )
        openDB()
        val flag = sqLiteDatabase.update(
            NOTE_TABLE_NAME, contentValues, "$NOTE_ID_COLUMN_NAME_INT = ?",
            arrayOf(note.getId().toString())
        )
        closeDB()
        return flag != 0
    }

    fun deleteNote(noteId: Int): Boolean {
        openDB()
        val flag = sqLiteDatabase.delete(
            NOTE_TABLE_NAME,
            "$NOTE_ID_COLUMN_NAME_INT = ?",
            arrayOf(noteId.toString())
        )
        closeDB()
        return flag != 0
    }

    fun getNote(noteId: Int): Note {
        openDB()
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $NOTE_TABLE_NAME where $NOTE_ID_COLUMN_NAME_INT = ?",
            arrayOf(noteId.toString())
        )
        var note: Note? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(NOTE_ID_COLUMN_NAME_INT))
            val title =
                cursor.getString(cursor.getColumnIndex(NOTE_TITLE_COLUMN_NAME_STRING))
            val body =
                cursor.getString((cursor.getColumnIndex(NOTE_BODY_COLUMN_NAME_STRING)))
            val background = cursor.getInt(
                cursor.getColumnIndex(
                    NOTE_BACKGROUND_COLUMN_NAME_INT
                )
            )
            note = Note.newInstance(
                id,
                title,
                body,
                Note.Background.fromIntToBackgroundClass(background)
            )
        }
        return note!!
    }

    fun getNotes(title: String): MutableList<Note> {
        openDB()
        val newTitle = "%$title%"
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $NOTE_TABLE_NAME where $NOTE_TITLE_COLUMN_NAME_STRING like ?",
            arrayOf(title)
        )
        val listOfNotes = mutableListOf<Note>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(NOTE_ID_COLUMN_NAME_INT))
            val background =
                cursor.getInt(cursor.getColumnIndex(NOTE_BACKGROUND_COLUMN_NAME_INT))
            val title =
                cursor.getString(cursor.getColumnIndex(NOTE_TITLE_COLUMN_NAME_STRING))
            val body =
                cursor.getString(cursor.getColumnIndex(NOTE_BODY_COLUMN_NAME_STRING))
            listOfNotes.add(
                Note.newInstance(
                    id,
                    title,
                    body,
                    Note.Background.fromIntToBackgroundClass(background)
                )
            )
        }
        return listOfNotes
    }


    fun getNotes(): MutableList<Note> {
        openDB()
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $NOTE_TABLE_NAME where $NOTE_TITLE_COLUMN_NAME_STRING NOT like ? And $NOTE_TITLE_COLUMN_NAME_STRING not like ?" +
                    " And $NOTE_TITLE_COLUMN_NAME_STRING not like ?" +
                    " And $NOTE_TITLE_COLUMN_NAME_STRING not like ?" +
                    " And $NOTE_TITLE_COLUMN_NAME_STRING not like ?",
            arrayOf("Card reader", "Receive printer", "Touch screen", "Dispenser", "Deposit")
        )
        val listOfNotes = mutableListOf<Note>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(NOTE_ID_COLUMN_NAME_INT))
            val background =
                cursor.getInt(cursor.getColumnIndex(NOTE_BACKGROUND_COLUMN_NAME_INT))
            val title =
                cursor.getString(cursor.getColumnIndex(NOTE_TITLE_COLUMN_NAME_STRING))
            val body =
                cursor.getString(cursor.getColumnIndex(NOTE_BODY_COLUMN_NAME_STRING))
            listOfNotes.add(
                Note.newInstance(
                    id,
                    title,
                    body,
                    Note.Background.fromIntToBackgroundClass(background)
                )
            )
        }
        return listOfNotes
    }

    @SuppressLint("Recycle")


    companion object {
        fun newInstance(mContext: Context): AccessDatabase {
            return AccessDatabase(mContext)
        }
    }

}