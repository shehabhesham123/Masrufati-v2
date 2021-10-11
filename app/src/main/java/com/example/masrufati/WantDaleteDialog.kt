package com.example.masrufati

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

private const val NOTE_CODE = "Note"
class Dialog: DialogFragment() {

    private lateinit var listener:DialogListener
    private lateinit var note: Note
    interface DialogListener{
        fun onClickYes(note: Note)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is DialogListener) listener = context
        else throw Exception("You don't implements DialogListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            try {
                note = it.getParcelable(NOTE_CODE)!!
            }catch (e:Exception){
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog : AlertDialog.Builder = AlertDialog.Builder(context!!)
        dialog.setMessage(resources.getString(R.string.messageDialog))
        dialog.setPositiveButton(resources.getString(R.string.yesDialog)) { _, _ -> listener.onClickYes(note)
        dismiss()}
        dialog.setNeutralButton(resources.getString(R.string.noDialog)){_,_ -> dismiss()}
        return dialog.create()
    }

    companion object{
        fun newInstance(note: Note):com.example.masrufati.Dialog {
            val args = Bundle()
            args.putParcelable(NOTE_CODE,note)
            val fragment = com.example.masrufati.Dialog()
            fragment.arguments = args
            return fragment
        }
    }
}
