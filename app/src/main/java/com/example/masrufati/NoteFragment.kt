package com.example.masrufati

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_note.*
import java.lang.ref.WeakReference


private const val CardReader = 1
private const val ReceivePrinter = 2
private const val TouchScreen = 3
private const val Dispenser = 4
private const val Deposit = 5
private const val Other = 6

class NoteFragment : Fragment() {

    private lateinit var accessDatabase: AccessDatabase
    private var cardReaderNotes = mutableListOf<Note>()
    private lateinit var cardReaderAdapter: Adapter
    private var receivePrinterNotes = mutableListOf<Note>()
    private lateinit var receivePrinterAdapter: Adapter
    private var touchScreenNotes = mutableListOf<Note>()
    private lateinit var touchScreenAdapter: Adapter
    private var dispenserNotes = mutableListOf<Note>()
    private lateinit var dispenserAdapter: Adapter
    private var depositNotes = mutableListOf<Note>()
    private lateinit var depositAdapter: Adapter
    private var otherNotes = mutableListOf<Note>()
    private lateinit var otherAdapter: Adapter

    // AsyncTask use to get all Note from database and run progress bar when getting data
    class Task(private val context: NoteFragment) : AsyncTask<Int, Unit, Int>() {
        // this use to get views and properties of NoteFragment
        private val activityReference: WeakReference<NoteFragment> = WeakReference(context)
        private val activity = activityReference.get()

        override fun onPreExecute() {
            super.onPreExecute()
            activity!!.NoteFragment_ProgressBar_CardReader.visibility = View.VISIBLE
            activity.NoteFragment_ProgressBar_Deposit.visibility = View.VISIBLE
            activity.NoteFragment_ProgressBar_Dispenser.visibility = View.VISIBLE
            activity.NoteFragment_ProgressBar_Other.visibility = View.VISIBLE
            activity.NoteFragment_ProgressBar_ReceivePrinter.visibility = View.VISIBLE
            activity.NoteFragment_ProgressBar_TouchScreen.visibility = View.VISIBLE

            activity.NoteFragment_RecyclerView_CardReader.visibility = View.INVISIBLE
            activity.NoteFragment_RecyclerView_Deposit.visibility = View.INVISIBLE
            activity.NoteFragment_RecyclerView_Dispenser.visibility = View.INVISIBLE
            activity.NoteFragment_RecyclerView_Other.visibility = View.INVISIBLE
            activity.NoteFragment_RecyclerView_ReceivePrinter.visibility = View.INVISIBLE
            activity.NoteFragment_RecyclerView_TouchScreen.visibility = View.INVISIBLE
        }

        override fun doInBackground(vararg p0: Int?): Int? {
            try {
                when (p0[0]) {
                    CardReader -> {
                        activity!!.cardReaderNotes = activity.accessDatabase.getNotes("Card reader")
                    }
                    ReceivePrinter -> {
                        activity!!.receivePrinterNotes =
                            activity.accessDatabase.getNotes("Receive printer")
                    }
                    TouchScreen -> {
                        activity!!.touchScreenNotes =
                            activity.accessDatabase.getNotes("Touch screen")
                    }
                    Dispenser -> {
                        activity!!.dispenserNotes = activity.accessDatabase.getNotes("Dispenser")
                    }
                    Deposit -> {
                        activity!!.depositNotes = activity.accessDatabase.getNotes("Deposit")
                    }
                    Other -> {
                        activity!!.otherNotes = activity.accessDatabase.getNotes()
                    }
                }
            } catch (e: Exception) {
            } finally {
                return p0[0]
            }
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            activity!!.NoteFragment_ProgressBar_CardReader.visibility = View.INVISIBLE
            activity.NoteFragment_ProgressBar_Deposit.visibility = View.INVISIBLE
            activity.NoteFragment_ProgressBar_Dispenser.visibility = View.INVISIBLE
            activity.NoteFragment_ProgressBar_Other.visibility = View.INVISIBLE
            activity.NoteFragment_ProgressBar_ReceivePrinter.visibility = View.INVISIBLE
            activity.NoteFragment_ProgressBar_TouchScreen.visibility = View.INVISIBLE

            if (activity.cardReaderNotes.size == 0) {
                activity.cardReader(false)
            } else {
                activity.cardReader(true)
            }

            if (activity.depositNotes.size == 0) {
                activity.deposit(false)
            } else {
                activity.deposit(true)
            }

            if (activity.dispenserNotes.size == 0) {
                activity.dispenser(false)
            } else {
                activity.dispenser(true)
            }

            if (activity.otherNotes.size == 0) {
                activity.other(false)
            } else {
                activity.other(true)
            }

            if (activity.receivePrinterNotes.size == 0) {
                activity.receivePrinter(false)
            } else {
                activity.receivePrinter(true)
            }

            if (activity.touchScreenNotes.size == 0) {
                activity.touchScreen(false)
            } else {
                activity.touchScreen(true)
            }

            when (result) {
                CardReader -> {
                    activity.insertDataIntoRecyclerView(CardReader)
                }
                ReceivePrinter -> {
                    activity.insertDataIntoRecyclerView(ReceivePrinter)
                }
                TouchScreen -> {
                    activity.insertDataIntoRecyclerView(TouchScreen)
                }
                Dispenser -> {
                    activity.insertDataIntoRecyclerView(Dispenser)
                }
                Deposit -> {
                    activity.insertDataIntoRecyclerView(Deposit)
                }
                Other -> {
                    activity.insertDataIntoRecyclerView(Other)
                }
            }

        }
    }

    fun touchScreen(isNoteFound: Boolean) {
        if (isNoteFound) {
            NoteFragment_RecyclerView_TouchScreen.visibility = View.VISIBLE
            NoteFragment_LinearLayout_TouchScreen_NoNote.visibility = View.INVISIBLE
        } else {
            NoteFragment_RecyclerView_TouchScreen.visibility = View.INVISIBLE
            NoteFragment_LinearLayout_TouchScreen_NoNote.visibility = View.VISIBLE
        }
    }

    fun receivePrinter(isNoteFound: Boolean) {
        if (isNoteFound) {
            NoteFragment_RecyclerView_ReceivePrinter.visibility = View.VISIBLE
            NoteFragment_LinearLayout_ReceivePrinter_NoNote.visibility = View.INVISIBLE
        } else {
            NoteFragment_RecyclerView_ReceivePrinter.visibility = View.INVISIBLE
            NoteFragment_LinearLayout_ReceivePrinter_NoNote.visibility = View.VISIBLE
        }
    }

    fun cardReader(isNoteFound: Boolean) {
        if (isNoteFound) {
            NoteFragment_RecyclerView_CardReader.visibility = View.VISIBLE
            NoteFragment_LinearLayout_CardReader_NoNote.visibility = View.INVISIBLE
        } else {
            NoteFragment_RecyclerView_CardReader.visibility = View.INVISIBLE
            NoteFragment_LinearLayout_CardReader_NoNote.visibility = View.VISIBLE
        }
    }

    fun dispenser(isNoteFound: Boolean) {
        if (isNoteFound) {
            NoteFragment_RecyclerView_Dispenser.visibility = View.VISIBLE
            NoteFragment_LinearLayout_Dispenser_NoNote.visibility = View.INVISIBLE
        } else {
            NoteFragment_RecyclerView_Dispenser.visibility = View.INVISIBLE
            NoteFragment_LinearLayout_Dispenser_NoNote.visibility = View.VISIBLE
        }
    }

    fun other(isNoteFound: Boolean) {
        if (isNoteFound) {
            NoteFragment_RecyclerView_Other.visibility = View.VISIBLE
            NoteFragment_LinearLayout_Other_NoNote.visibility = View.INVISIBLE
        } else {
            NoteFragment_RecyclerView_Other.visibility = View.INVISIBLE
            NoteFragment_LinearLayout_Other_NoNote.visibility = View.VISIBLE
        }
    }

    fun deposit(isNoteFound: Boolean) {
        if (isNoteFound) {
            NoteFragment_RecyclerView_Deposit.visibility = View.VISIBLE
            NoteFragment_LinearLayout_Deposit_NoNote.visibility = View.INVISIBLE
        } else {
            NoteFragment_RecyclerView_Deposit.visibility = View.INVISIBLE
            NoteFragment_LinearLayout_Deposit_NoNote.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        try {
            accessDatabase = AccessDatabase.newInstance(context!!)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "An error has occurred when connecting with the database",
                Toast.LENGTH_SHORT
            ).show()
        }

        try {
            Task(this).execute(CardReader)       // use AsyncTask to get all notes of title (CardReader)
            Task(this).execute(ReceivePrinter)       // use AsyncTask to get all notes of title (ReceivePrinter)
            Task(this).execute(TouchScreen)       // use AsyncTask to get all notes of title (TouchScreen)
            Task(this).execute(Dispenser)       // use AsyncTask to get all notes of title (Dispenser)
            Task(this).execute(Deposit)       // use AsyncTask to get all notes of title (Deposit)
            Task(this).execute(Other)       // use AsyncTask to get all notes of title (Deposit)
        } catch (e: Exception) {
            Toast.makeText(
                context!!,
                "An error has occurred when getting all Note",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note, container, false)
    }


    // make adapter and connect it with recyclerView
    @SuppressLint("UseCompatLoadingForColorStateLists")
    fun insertDataIntoRecyclerView(typeOfNote: Int) {
        val recyclerView: RecyclerView
        val adapter = when (typeOfNote) {
            CardReader -> {
                recyclerView = NoteFragment_RecyclerView_CardReader
                reverseNote(cardReaderNotes)
                cardReaderAdapter =
                    Adapter(cardReaderNotes) { idOfColor -> resources.getColorStateList(idOfColor) }
                cardReaderAdapter

            }
            ReceivePrinter -> {
                recyclerView = NoteFragment_RecyclerView_ReceivePrinter
                reverseNote(receivePrinterNotes)
                receivePrinterAdapter = Adapter(receivePrinterNotes) { idOfColor ->
                    resources.getColorStateList(idOfColor)
                }
                receivePrinterAdapter
            }
            TouchScreen -> {
                recyclerView = NoteFragment_RecyclerView_TouchScreen
                reverseNote(touchScreenNotes)
                touchScreenAdapter =
                    Adapter(touchScreenNotes) { idOfColor -> resources.getColorStateList(idOfColor) }
                touchScreenAdapter
            }
            Dispenser -> {
                recyclerView = NoteFragment_RecyclerView_Dispenser
                reverseNote(dispenserNotes)
                dispenserAdapter =
                    Adapter(dispenserNotes) { idOfColor -> resources.getColorStateList(idOfColor) }
                dispenserAdapter

            }
            Deposit -> {
                recyclerView = NoteFragment_RecyclerView_Deposit
                reverseNote(depositNotes)
                depositAdapter =
                    Adapter(depositNotes) { idOfColor -> resources.getColorStateList(idOfColor) }
                depositAdapter
            }
            else -> {
                recyclerView = NoteFragment_RecyclerView_Other
                reverseNote(otherNotes)
                otherAdapter =
                    Adapter(otherNotes) { idOfColor -> resources.getColorStateList(idOfColor) }
                otherAdapter
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)

    }

    private fun whichListNotes(note: Note): Pair<MutableList<Note>, (Boolean) -> Unit> {
        return when {
            note.getTitle() == "Card reader" -> {
                Pair(cardReaderNotes, { isFound: Boolean ->
                    if (isFound) {
                        NoteFragment_RecyclerView_CardReader.visibility = View.VISIBLE
                        NoteFragment_LinearLayout_CardReader_NoNote.visibility = View.INVISIBLE
                    } else {
                        NoteFragment_RecyclerView_CardReader.visibility = View.INVISIBLE
                        NoteFragment_LinearLayout_CardReader_NoNote.visibility = View.VISIBLE
                    }
                })
            }
            note.getTitle() == "Receive printer" -> {
                Pair(receivePrinterNotes, { isFound: Boolean ->
                    if (isFound) {
                        NoteFragment_RecyclerView_ReceivePrinter.visibility = View.VISIBLE
                        NoteFragment_LinearLayout_ReceivePrinter_NoNote.visibility = View.INVISIBLE
                    } else {
                        NoteFragment_RecyclerView_ReceivePrinter.visibility = View.INVISIBLE
                        NoteFragment_LinearLayout_ReceivePrinter_NoNote.visibility = View.VISIBLE
                    }
                })
            }
            note.getTitle() == "Touch screen" -> {
                Pair(touchScreenNotes, { isFound: Boolean ->
                    if (isFound) {
                        NoteFragment_RecyclerView_TouchScreen.visibility = View.VISIBLE
                        NoteFragment_LinearLayout_TouchScreen_NoNote.visibility = View.INVISIBLE
                    } else {
                        NoteFragment_RecyclerView_TouchScreen.visibility = View.INVISIBLE
                        NoteFragment_LinearLayout_TouchScreen_NoNote.visibility = View.VISIBLE
                    }
                })
            }
            note.getTitle() == "Dispenser" -> {
                Pair(dispenserNotes, { isFound: Boolean ->
                    if (isFound) {
                        NoteFragment_RecyclerView_Dispenser.visibility = View.VISIBLE
                        NoteFragment_LinearLayout_Dispenser_NoNote.visibility = View.INVISIBLE
                    } else {
                        NoteFragment_RecyclerView_Dispenser.visibility = View.INVISIBLE
                        NoteFragment_LinearLayout_Dispenser_NoNote.visibility = View.VISIBLE
                    }
                })
            }
            note.getTitle() == "Deposit" -> {
                Pair(depositNotes, { isFound: Boolean ->
                    if (isFound) {
                        NoteFragment_RecyclerView_Deposit.visibility = View.VISIBLE
                        NoteFragment_LinearLayout_Deposit_NoNote.visibility = View.INVISIBLE
                    } else {
                        NoteFragment_RecyclerView_Deposit.visibility = View.INVISIBLE
                        NoteFragment_LinearLayout_Deposit_NoNote.visibility = View.VISIBLE
                    }
                })
            }
            else -> {
                Pair(otherNotes, { isFound: Boolean ->
                    if (isFound) {
                        NoteFragment_RecyclerView_Other.visibility = View.VISIBLE
                        NoteFragment_LinearLayout_Other_NoNote.visibility = View.INVISIBLE
                    } else {
                        NoteFragment_RecyclerView_Other.visibility = View.INVISIBLE
                        NoteFragment_LinearLayout_Other_NoNote.visibility = View.VISIBLE
                    }
                })
            }
        }
    }

    private fun whichListNotes(typeOfNote: Int): MutableList<Note> {
        return when (typeOfNote) {
            CardReader -> {
                cardReaderNotes
            }
            ReceivePrinter -> {
                receivePrinterNotes
            }
            TouchScreen -> {
                touchScreenNotes
            }
            Dispenser -> {
                dispenserNotes
            }
            Deposit -> {
                depositNotes
            }
            else -> {
                otherNotes
            }
        }
    }

    private fun reverseNote(notes: MutableList<Note>) {
        notes.reverse()
    }

    fun newNoteIsAdded(note: Note): Boolean {
        try {
            val id = accessDatabase.newNote(note)
            note.setId(id)
            val title = note.getTitle()
            note.setTitle(if(title!![title.length-1]==' ') title.removeRange(title.length-1,title.length)
            else title)

            when {
                note.getTitle() == "Card reader" -> {
                    cardReaderNotes.add(0, note)
                    if (cardReaderNotes.size == 1) cardReader(true)
                    cardReaderAdapter.notifyDataSetChanged()
                }
                note.getTitle() == "Receive printer" -> {
                    receivePrinterNotes.add(0, note)
                    if (receivePrinterNotes.size == 1) receivePrinter(true)
                    receivePrinterAdapter.notifyDataSetChanged()
                }
                note.getTitle() == "Touch screen" -> {
                    touchScreenNotes.add(0, note)
                    if (touchScreenNotes.size == 1) touchScreen(true)
                    touchScreenAdapter.notifyDataSetChanged()
                }
                note.getTitle() == "Dispenser" -> {
                    dispenserNotes.add(0, note)
                    if (dispenserNotes.size == 1) dispenser(true)
                    dispenserAdapter.notifyDataSetChanged()
                }
                note.getTitle() == "Deposit" -> {
                    depositNotes.add(0, note)
                    if (depositNotes.size == 1) deposit(true)
                    depositAdapter.notifyDataSetChanged()
                }
                else -> {
                    otherNotes.add(0, note)
                    if (otherNotes.size == 1) other(true)
                    otherAdapter.notifyDataSetChanged()
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun updateNote(note: Note): Boolean {
        return try {
            val title = note.getTitle()
            note.setTitle(if(title!![title.length-1]==' ') title.removeRange(title.length-1,title.length)
            else title)
            accessDatabase.updateNote(note)
            Task(this).execute(CardReader)       // use AsyncTask to get all notes of title (CardReader)
            Task(this).execute(ReceivePrinter)       // use AsyncTask to get all notes of title (ReceivePrinter)
            Task(this).execute(TouchScreen)       // use AsyncTask to get all notes of title (TouchScreen)
            Task(this).execute(Dispenser)       // use AsyncTask to get all notes of title (Dispenser)
            Task(this).execute(Deposit)       // use AsyncTask to get all notes of title (Deposit)
            Task(this).execute(Other)       // use AsyncTask to get all notes of title (other)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun delete(notes: MutableList<Note>,note: Note):Boolean{
        for(i in notes){
            if(i.getId() == note.getId()){
                notes.remove(i)
                return true
            }
        }
        return false
    }

    fun deleteNote(note:Note):Boolean{
        try {
            val isDelete = accessDatabase.deleteNote(note.getId())
            return if(isDelete){
                when(note.getTitle()){
                    "Card reader" ->{
                        delete(cardReaderNotes,note)
                        if(cardReaderNotes.size == 0) cardReader(false)
                        cardReaderAdapter.notifyDataSetChanged()

                    }
                    "Receive printer" ->{
                        delete(receivePrinterNotes,note)
                        if(receivePrinterNotes.size == 0) receivePrinter(false)
                        receivePrinterAdapter.notifyDataSetChanged()
                    }
                    "Touch screen" ->{
                        delete(touchScreenNotes,note)
                        if(touchScreenNotes.size == 0) touchScreen(false)
                        touchScreenAdapter.notifyDataSetChanged()
                    }
                    "Dispenser" ->{
                        delete(dispenserNotes,note)
                        if(dispenserNotes.size == 0) dispenser(false)
                        dispenserAdapter.notifyDataSetChanged()
                    }
                    "Deposit" -> {
                        delete(depositNotes,note)
                        if(depositNotes.size == 0) deposit(false)
                        depositAdapter.notifyDataSetChanged()
                    }
                    else ->{
                        delete(otherNotes,note)
                        if(otherNotes.size == 0) other(false)
                        otherAdapter.notifyDataSetChanged()
                    }
                }
                true
            }
            else{
                false
            }
        }catch (e:Exception){
            return false
        }

    }

}

class Adapter(
    private val notes: MutableList<Note>,
    private val lambdaColor: (Int) -> ColorStateList    // lambda get get color from color file
) : RecyclerView.Adapter<Adapter.VH>() {
    class VH(viewItem: View) : RecyclerView.ViewHolder(viewItem)

    private lateinit var listener:OneNoteListener

    interface OneNoteListener{
        fun onNoteClickToViewOrUpdate(note: Note)
        fun onNoteLongClickToDelete(note: Note)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if(recyclerView.context is OneNoteListener) listener = recyclerView.context as OneNoteListener
        else throw Exception("You don't implements from OneNoteListener")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.one_note, null, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val view = holder.itemView
        val text = view.findViewById<TextView>(R.id.OneNote_TextView)
        val background = view.findViewById<CardView>(R.id.OneNote_CardView)
        text.text = "${notes[position].getBody()}"
        when (notes[position].getBackground()) {
            Note.Background.WHITE -> background.backgroundTintList =
                lambdaColor(android.R.color.white)
            Note.Background.BLUE -> background.backgroundTintList =
                lambdaColor(android.R.color.holo_blue_bright)
            Note.Background.GRAY -> background.backgroundTintList =
                lambdaColor(android.R.color.darker_gray)
            Note.Background.PURPLE -> background.backgroundTintList =
                lambdaColor(android.R.color.holo_purple)
        }

        view.setOnClickListener {
            listener.onNoteClickToViewOrUpdate(notes[position])
        }

        view.setOnLongClickListener {
            listener.onNoteLongClickToDelete(notes[position])
            true
        }
    }
}