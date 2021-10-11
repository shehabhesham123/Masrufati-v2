package com.example.masrufati

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_trip.*
import java.lang.ref.WeakReference
import kotlin.math.ceil

@Suppress("DEPRECATION")
class ShowTripsFragment() : Fragment() {

    private var tripClasses: MutableList<Trip> = mutableListOf()
    private lateinit var adapter: Adapter
    private lateinit var accessDatabase: AccessDatabase

    // AsyncTask use to get all trips from database and run progress bar when getting data
    class Task(private val context: ShowTripsFragment) : AsyncTask<Unit, Unit, Unit>() {
        // this use to get views and properties of TripFragment
        private val activityReference: WeakReference<ShowTripsFragment> = WeakReference(context)
        private val activity = activityReference.get()

        override fun onPreExecute() {
            super.onPreExecute()
            activity!!.TripFragment_FrameLayout.visibility = View.GONE
            activity.TripFragment_ProgressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg p0: Unit?) {
            activity!!.tripClasses = activity.accessDatabase.getAllTrip()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            activity!!.TripFragment_ProgressBar.visibility = View.GONE
            activity.TripFragment_FrameLayout.visibility = View.VISIBLE

            activity.reverseTripsByDate()        // reverse trips because trips in database sort in ASE and here need DESC
            activity.isTripsFound(activity.tripClasses)      // if there are trips -> invisible to (No trips) and visible to (RecyclerView) else -> otherwise
            activity.insertDataIntoRecyclerView()       // generate adapter in connect it with recyclerview
        }
    }

    override fun onStart() {
        super.onStart()

        try {
            accessDatabase = AccessDatabase.newInstance(context!!)      // get access to database
        } catch (e: Exception) {
            Toast.makeText(
                context!!,
                "An error has occurred when connecting with the database",
                Toast.LENGTH_LONG
            ).show()
        }

        try {
            Task(this).execute()       // use AsyncTask to get all trips
        } catch (e: Exception) {
            Toast.makeText(
                context!!,
                "An error has occurred when getting all trips",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_trip, container, false)
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private fun insertDataIntoRecyclerView() {
        adapter = Adapter(
            tripClasses,
            context!!,
            { idOfString -> resources.getString(idOfString) },  // lambda get get string from string file
            { idOfColor -> resources.getColorStateList(idOfColor) })    // lambda get get color from color file

        TripFragment_RecyclerView.setHasFixedSize(true)
        TripFragment_RecyclerView.adapter = adapter
        TripFragment_RecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun reverseTripsByDate() {
        tripClasses.reverse()
    }

    //fun use to {if there are trip -> visible to recyclerView adn hide NoTrip else otherwise}
    private fun isTripsFound(tripClasses: MutableList<Trip>) {
        if (tripClasses.size == 0) {
            TripFragment_RecyclerView.visibility = View.GONE
            TripFragment_LinearLayout_NoTrip.visibility = View.VISIBLE
        } else {
            TripFragment_RecyclerView.visibility = View.VISIBLE
            TripFragment_LinearLayout_NoTrip.visibility = View.GONE
        }
    }

    fun generatedSearchWithDate(date: String):Pair<Int,Int> {
        var size = 0
        var totalCost = 0
        try {
            val trips = accessDatabase.getAllTrip(date)
            size = trips.size
            totalCost = getTotalCost(trips)
            isTripsFound(trips)
            adapter.changeTrips(trips)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        return Pair(size,totalCost)
    }

    private fun getTotalCost(tripClasses: MutableList<Trip>): Int {
        var sum = 0
        for (i in tripClasses) {
            sum += i.getCost()
        }
        return sum
    }

    fun cancelSearch() {
        isTripsFound(this.tripClasses)
        adapter.changeTrips(this.tripClasses)
        adapter.notifyDataSetChanged()
    }

    fun newTripIsAdded(trip: Trip?,returnTrip:Boolean) {
        try {
            val tripAdded = if(!returnTrip){
                val id = accessDatabase.newTrip(trip!!)      // trip that get in this function is without id ----> newTrip() return its id
                trip.setId(id)
                trip
            }else{
                val lastTrip = accessDatabase.getLastTrip()
                val id = accessDatabase.newTrip(lastTrip)
                lastTrip.setId(id)
                lastTrip
            }
            tripClasses.add(0, tripAdded)
            isTripsFound(this.tripClasses)
            adapter.notifyItemInserted(0)
            TripFragment_RecyclerView.smoothScrollToPosition(0)

        } catch (e: NumberFormatException) {
            Toast.makeText(
                context,
                "An error has occurred when record new trip in database",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun updateTrip(tripClass: Trip): Boolean {
        var isUpdate = false
        try {
            isUpdate = accessDatabase.updateTrip(tripClass)  // update trip in database
            if (isUpdate) {   // if success update ----> update it in list in this fragment
                for (i in 0 until tripClasses.size) {
                    if (tripClasses[i].getId() == tripClass.getId()) {
                        tripClasses[i] = tripClass
                        adapter.notifyItemChanged(i)
                        break
                    }
                }
            }
        } catch (e: Exception) {   // error occur when updating ----> isUpdate = false
            isUpdate = false
        } finally {
            return isUpdate
        }
    }

    fun deleteTrip(tripId: Int): Boolean {
        var isRemoved = false
        try {
            val trip =
                accessDatabase.getTrip(tripId)   // get trip from database that its id equal tripId
            isRemoved = accessDatabase.removeTrip(tripId)
            if (isRemoved) {    // if success removed ----> removed it in list in this fragment
                for (i in 0 until tripClasses.size) {
                    if (tripClasses[i].getId() == trip.getId()) {
                        tripClasses.removeAt(i)
                        adapter.notifyDataSetChanged()
                        break
                    }
                }
                isTripsFound(this.tripClasses)    // check if there are trips in list ---- may be this trip was last trip in list that mean that no trip found in list so show (NoTrip)
            }
        } catch (e: Exception) {   // error occur when removing ----> isRemoved = false
            isRemoved = false
        } finally {
            return isRemoved
        }
    }

    class Adapter(
        private var tripClasses: MutableList<Trip>,
        private val mContext: Context,
        private val lambdaString: (Int) -> String,      // lambda get get string from string file
        private val lambdaColor: (Int) -> ColorStateList    // lambda get get color from color file
    ) : RecyclerView.Adapter<Adapter.VH>() {

        class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

        private lateinit var listener: TripAdapterListener  // listener use when click on setting(update, delete, addNote)

        interface TripAdapterListener {
            fun onSettingClick(tripClass: Trip)
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            if (mContext is TripAdapterListener) listener = mContext
            else throw Exception("You don't implements from TripAdapterListener")
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(mContext).inflate(R.layout.one_trip, parent, false)
            return VH(view)
        }

        override fun getItemCount(): Int {
            return tripClasses.size
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: VH, position: Int) {

            val view = holder.itemView

            val layout =
                view.findViewById<ConstraintLayout>(R.id.layout_trip)      // to change background
            val duration = view.findViewById<TextView>(R.id.OneTrip_TextView_Duration)
            val initialLocation = view.findViewById<TextView>(R.id.OneTrip_TextView_InitialLocation)
            val finalLocation = view.findViewById<TextView>(R.id.OneTrip_TextView_FinalLocation)
            val distance = view.findViewById<TextView>(R.id.OneTrip_TextView_Distance)
            val setting = view.findViewById<ImageView>(R.id.OneTrip_ImageView_Setting)

            layout.backgroundTintList = generateBackgroundColor()       // change background color

            animation(view, position)   // make animation to view

            duration.text = "${calculationDuration(position)}, "
            initialLocation.text = "${tripClasses[position].getInitialLocation()}, "
            finalLocation.text = "${tripClasses[position].getFinalLocation()}, "
            distance.text = tripClasses[position].getCost().toString()

            setting.setOnClickListener {
                listener.onSettingClick(tripClasses[position])
            }
        }

        // fun use to calculation duration from trip date to current date
        private fun calculationDuration(position: Int): String {
            return when (Date.difference(tripClasses[position].getDate(), Date.currentDate())) {
                0 -> lambdaString(R.string.on_this_day)
                1 -> lambdaString(R.string.one_day_ago)
                2 -> lambdaString(R.string.two_days_ago)
                else -> "${lambdaString(R.string.on)} ${tripClasses[position].getDate()}"
            }
        }

        // generate random background
        private fun generateBackgroundColor(): ColorStateList {
            val colors = arrayOf(
                android.R.color.holo_purple,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_light,
                android.R.color.darker_gray,
                R.color.blue_light,
                R.color.pink,
                R.color.green_dark,
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4
            )

            val random = Math.random()
            return lambdaColor(colors[ceil(random * colors.size - 1).toInt()])
        }

        // function to change trips that want to show them in recyclerView
        fun changeTrips(tripClasses: MutableList<Trip>) {
            this.tripClasses = tripClasses
        }

        private fun animation(view: View, position: Int) {
            var duration = 100L
            if (position < 6) {
                duration += (position * 75)
            } else {
                duration = 300L
            }
            view.alpha = 0f
            view.translationX = -200f
            view.animate().alpha(1f).setDuration(duration+50).start()
            view.animate().translationX(0f).setDuration(duration).start()
            view.translationX = 0f  // to when add new trip its view will be in translationX = 0 not -200
        }

    }

}

