package com.example.masrufati

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_write_note.*

private const val NOTE_CODE = "note"

class NoteClassWriteNoteFragment : DialogFragment() {

    private lateinit var listener: NoteListener
    private var note: Note? = null

    interface NoteListener {
        fun onNoteDoneClick(note: Note)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NoteListener) listener = context
        else throw Exception("You don't implements from NoteListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            try {
                note = it.getParcelable(NOTE_CODE)
                if(note== null) note = Note.newInstance("","",Note.Background.WHITE)
            }catch (e:Exception){
                Toast.makeText(context, "An error occurred when doing this operation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_write_note, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            // change background
            when (note!!.getBackground()) {
                Note.Background.BLUE -> WriteNoteFragment_LinearLayout_background.background =
                    resources.getDrawable(android.R.color.holo_blue_bright)
                Note.Background.GRAY -> WriteNoteFragment_LinearLayout_background.background =
                    resources.getDrawable(android.R.color.darker_gray)
                Note.Background.PURPLE -> WriteNoteFragment_LinearLayout_background.background =
                    resources.getDrawable(android.R.color.holo_purple)
                else -> WriteNoteFragment_LinearLayout_background.background =
                    resources.getDrawable(android.R.color.white)
            }
            // put data in views
            WriteNoteFragment_TextInputEditText_Title.setText(note?.getTitle())
            WriteNoteFragment_TextInputEditText_Body.setText(note?.getBody())
            WriteNoteFragment_TextView_NumOfLetters.text = "${WriteNoteFragment_TextInputEditText_Body.text.toString().length}/1000"

        }catch (e:Exception){
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
        }


        try {

            // listener to change background color
            WriteNoteFragment_CardView_blue.setOnClickListener {
                WriteNoteFragment_LinearLayout_background.background = resources.getDrawable(android.R.color.holo_blue_bright)
                note!!.setBackground(Note.Background.BLUE)
            }
            WriteNoteFragment_CardView_gray.setOnClickListener {
                WriteNoteFragment_LinearLayout_background.background = resources.getDrawable(android.R.color.darker_gray)
                note!!.setBackground(Note.Background.GRAY)
            }
            WriteNoteFragment_CardView_purple.setOnClickListener {
                WriteNoteFragment_LinearLayout_background.background = resources.getDrawable(android.R.color.holo_purple)
                note!!.setBackground(Note.Background.PURPLE)
            }
            WriteNoteFragment_CardView_white.setOnClickListener {
                WriteNoteFragment_LinearLayout_background.background = resources.getDrawable(android.R.color.white)
                note!!.setBackground( Note.Background.WHITE)
            }

            WriteNoteFragment_ImageView_Done.setOnClickListener {
                val title = WriteNoteFragment_TextInputEditText_Title.text.toString()
                val body = WriteNoteFragment_TextInputEditText_Body.text.toString()

                this.note!!.setTitle(title)
                this.note!!.setBody(body)

                listener.onNoteDoneClick(note!!)
                dismiss()
            }

            // textWatcher
            WriteNoteFragment_TextInputEditText_Body.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                @SuppressLint("SetTextI18n")
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    WriteNoteFragment_TextView_NumOfLetters.text = "${p0?.length}/1000"
                }
            })
        }catch (e:Exception){

        }

    }

    companion object {
        fun newInstance(note: Note?): NoteClassWriteNoteFragment {
            val args = Bundle()
            args.putParcelable(NOTE_CODE,note)
            val fragment = NoteClassWriteNoteFragment()
            fragment.arguments = args
            return fragment
        }

    }

}