package com.example.masrufati

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_setting_for_one_trip.*

private const val TRIP_ID_CODE = "trip_id"

class SettingTripFragment : DialogFragment() {

    private var tripId: Int = -1
    private lateinit var listener: SettingListener      // listener use when click on (update, delete, addNote)

    interface SettingListener {
        fun updateClick(tripId: Int)
        fun deleteClick(tripId: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SettingListener) listener = context
        else throw Exception("You don't implements from Setting interface")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tripId = it.getInt(TRIP_ID_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting_for_one_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SettingForOneTripFragment_TextView_Update.setOnClickListener {
            listener.updateClick(tripId)
            dismiss()
        }
        SettingForOneTripFragment_TextView_Delete.setOnClickListener {
            listener.deleteClick(tripId)
            dismiss()
        }
    }

    companion object {
        fun newInstance(tripId: Int): SettingTripFragment {
            val args = Bundle()
            args.putInt(TRIP_ID_CODE, tripId)
            val fragment = SettingTripFragment()
            fragment.arguments = args
            return fragment
        }
    }
}