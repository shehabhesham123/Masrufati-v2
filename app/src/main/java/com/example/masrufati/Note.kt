package com.example.masrufati

import android.os.Parcel
import android.os.Parcelable
import java.lang.NumberFormatException

class Note private constructor(
    private var id:Int,
    private var title: String?,
    private var body: String?,
    private var background: Int
) : Parcelable , Validation{

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    fun getId() = checkId(this.id)

    fun setId(id: Int){
        this.id = checkId(id)
    }

    fun getTitle() = this.title

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getBody() = this.body

    fun setBody(body: String?) {
        this.body = body
    }

    fun getBackground() = Background.fromIntToBackgroundClass(this.background)

    fun setBackground(background: Background) {
        this.background = Background.fromBackgroundClassToInt(background)
    }

    override fun checkId(id: Int): Int {
        if(id > -1) return id
        throw NumberFormatException("invalid id")
    }

    enum class Background {
        PURPLE,
        WHITE,
        GRAY,
        BLUE;

        companion object {
            fun fromBackgroundClassToInt(background: Background): Int {       // to insert in database
                return when (background) {
                    PURPLE -> 1
                    WHITE -> 2
                    GRAY -> 3
                    else -> 4
                }
            }

            fun fromIntToBackgroundClass(background: Int): Background {        // to change from database int to background
                return when (background) {
                    1 -> PURPLE
                    2 -> WHITE
                    3 -> GRAY
                    else -> BLUE
                }
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeInt(background)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }

        fun newInstance(id:Int, title: String?, body: String?, background: Background): Note {
            return Note(id, title, body, Background.fromBackgroundClassToInt(background))
        }

        fun newInstance(title: String?, body: String?, background: Background): Note {
            return Note(-1, title, body, Background.fromBackgroundClassToInt(background))
        }
    }
}

