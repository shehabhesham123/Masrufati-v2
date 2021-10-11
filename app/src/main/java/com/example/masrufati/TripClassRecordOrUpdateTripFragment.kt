package com.example.masrufati

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_new_trip.*

private const val TRIP_ID = "TRIP_ID"
private const val INITIAL_LOCATION = "INITIAL_LOCATION"
private const val FINAL_LOCATION = "FINAL_LOCATION"
private const val DISTANCE = "DISTANCE"

class RecordTripFragment : DialogFragment(), Validation, Formation {

    private lateinit var listener: NewTripFragmentListener

    private var tripId: Int = 0
    private var initialLocation: String? = null
    private var finalLocation: String? = null
    private var distance: Int = 1

    interface NewTripFragmentListener {
        fun onDoneClick(tripClass: Trip, isUpdate: Boolean)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NewTripFragmentListener) listener = context
        else throw Exception("You don't implements from NewTripFragment")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            initialLocation = it.getString(INITIAL_LOCATION)
            finalLocation = it.getString(FINAL_LOCATION)
            distance = it.getInt(DISTANCE)
            tripId = it.getInt(TRIP_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_trip, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NewTripFragment_NumberPicker_Kilometer.maxValue = 3000
        NewTripFragment_NumberPicker_Kilometer.minValue = 1
        insertDateToUpdateIt()

        NewTripFragment_Button_Done.setOnClickListener {
            val initialLocation = NewTripFragment_TextInputEditText_InitialLocation.text.toString()
            val finalLocation = NewTripFragment_TextInputEditText_FinalLocation.text.toString()
            val kilometer = NewTripFragment_NumberPicker_Kilometer.value

            if (valid(initialLocation,finalLocation)) {
                val trip = Trip(initialLocation, finalLocation, kilometer)
                trip.setId(tripId)      // if this is to update ... the id is valid else tripId = 0 and in main will insert true id
                listener.onDoneClick(trip, this.initialLocation != null)
                dismiss()
            }
        }
    }

    // function to valid data from views
    private fun valid(initialLocation:String,finalLocation:String):Boolean{
        var valid = true
        try {
            checkString(initialLocation)
        } catch (e: NullPointerException) {
            valid = false
            NewTripFragment_TextInputEditText_InitialLocation.error = resources.getString(R.string.Error3)
        }
        try {
            checkString(finalLocation)
        } catch (e: NullPointerException) {
            valid = false
            NewTripFragment_TextInputEditText_FinalLocation.error =  resources.getString(R.string.Error3)
        }

        return valid
    }

    // function to put data into views
    private fun insertDateToUpdateIt() {
        NewTripFragment_TextInputEditText_InitialLocation.setText(initialLocation)
        NewTripFragment_TextInputEditText_FinalLocation.setText(finalLocation)
        NewTripFragment_NumberPicker_Kilometer.value = distance
    }

    override fun checkString(string: String?): String {
        return if (!(string.isNullOrEmpty() || string.isNullOrBlank() || string == "")) formatString(
            string
        )
        else throw NullPointerException("Empty String in class Trip line 44")
    }

    override fun formatString(string: String): String {
        var newString = ""
        for (i in string) {
            if (i == ' ' && newString != "" && newString[newString.length - 1] != ' ') newString += i
            else if (i != ' ') newString += i
        }
        return newString
    }

    companion object {

        fun newInstance(tripClass: Trip): RecordTripFragment {
            val bundle = Bundle()
            bundle.putInt(TRIP_ID, tripClass.getId())
            bundle.putString(INITIAL_LOCATION, tripClass.getInitialLocation())
            bundle.putString(FINAL_LOCATION, tripClass.getFinalLocation())
            bundle.putInt(DISTANCE, tripClass.getCost())
            val fragment = RecordTripFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}