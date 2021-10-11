package com.example.masrufati

import android.animation.Animator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_app_bar.*
import kotlin.math.hypot

private var NOTIFICATION_ID: Int = 16321
private var CHANNEL_NAME = "CHANNEL"
private var CHANNEL_ID = "16666"

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    RecordTripFragment.NewTripFragmentListener,
    ShowTripsFragment.Adapter.TripAdapterListener, SettingTripFragment.SettingListener,
    CalenderFragment.CalenderListener,NoteClassWriteNoteFragment.NoteListener,Adapter.OneNoteListener,
Dialog.DialogListener{

    private var isAddClicked = false
    private var isSearch = false
    private var isTripFragmentShow = false
    private var addFabRotateInitialDegree = 0f;
    private var tripClassFragment: ShowTripsFragment? = null
    private var isNoteFragmentShow = false
    private var noteClassFragment: NoteFragment? = null
    private lateinit var db: AccessDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = AccessDatabase.newInstance(baseContext)
        navigationView()
        showTripFragment()

        MainActivity_FAB_AddTrip.setOnClickListener {
            clickSound()
            if(isTripFragmentShow){
                if (isAddClicked) addClick(false)
                else addClick(true)
            }else{
                val dialog = NoteClassWriteNoteFragment.newInstance(null)
                dialog.show(supportFragmentManager,null)
            }

        }

        MainActivity_FAB_NewTrip.setOnClickListener {
            clickSound()
            newTrip()
        }

        MainActivity_FAB_ReturnTrip.setOnClickListener {
            clickSound()
            returnTrip()
        }

        MainActivity_FAB_Search.setOnClickListener {
            clickSound()
            search()
        }

        MainActivity_ImageView_ArrowToReturn.setOnClickListener {
            clickSound()
            arrowReturn()
        }
    }

    private fun navigationView() {
        MainActivity_BottomNavigationView.background = null
        MainActivity_BottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    // this is call at the first open the app
    private fun showTripFragment() {
        if (tripClassFragment == null) tripClassFragment = ShowTripsFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.MainActivity_FrameLayout_FragmentContainer, tripClassFragment!!)
        fragmentTransaction.commit()
        isTripFragmentShow = true
    }

    private fun showFragmentWithAnimation(fragment: Fragment) {
        val enterAnimation: Int
        val exitAnimation: Int
        if (fragment is ShowTripsFragment) {
            exitAnimation = R.anim.out_right
            enterAnimation = R.anim.in_left
        } else {
            exitAnimation = R.anim.out_left
            enterAnimation = R.anim.in_right
        }

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            enterAnimation,
            exitAnimation,
            enterAnimation,
            exitAnimation
        )
        fragmentTransaction.replace(R.id.MainActivity_FrameLayout_FragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        clickSound()

        try {
            addClick(false)

            MainActivity_ImageView_ArrowToReturn.visibility = View.GONE
            isSearch = false

            when (item.itemId) {
                R.id.BottomNavigationViewMenu_Trips -> {
                    // because the user may be click in same fragment and it load again ---> this prevent it
                    if (isNoteFragmentShow) {
                        if (tripClassFragment == null) tripClassFragment = ShowTripsFragment()
                        showFragmentWithAnimation(tripClassFragment!!)
                        isTripFragmentShow = true
                        isNoteFragmentShow = false
                    }
                }
                else -> {
                    if (isTripFragmentShow) {
                        if (noteClassFragment == null) noteClassFragment = NoteFragment()
                        showFragmentWithAnimation(noteClassFragment!!)
                        isTripFragmentShow = false
                        isNoteFragmentShow = true
                    }
                }
            }
        }catch (e:Exception){ }
        return true
    }

    private fun arrowReturn() {
        try {
            addClick(false)
            tripClassFragment!!.cancelSearch()
            MainActivity_ImageView_ArrowToReturn.visibility = View.GONE
            isSearch = false
        }catch (e:Exception){}
    }

    private fun cancelSearch(){
        if(isSearch){
            tripClassFragment?.cancelSearch()
            MainActivity_ImageView_ArrowToReturn.visibility = View.GONE
            isSearch = false
        }
    }

    private fun search() {
        addClick(false)
        cancelSearch()
        val dialog = CalenderFragment()
        dialog.show(supportFragmentManager, null)
    }

    private fun returnTrip() {
        if (isSearch) {
            arrowReturn()
        }
        addClick(false)
        cancelSearch()
        try {
            tripClassFragment!!.newTripIsAdded(null , true)
        } catch (e: Exception) {
            Snackbar.make(
                MainActivity_FrameLayout_FragmentContainer,
                resources.getString(R.string.Error2),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun newTrip() {
        if (isSearch) {
            arrowReturn()
        }
        addClick(false)
        cancelSearch()
        val dialog = RecordTripFragment()
        dialog.show(supportFragmentManager, null)
    }

    private fun addClick(isClicked:Boolean) {
        rotateFab(MainActivity_FAB_AddTrip)
        if (isClicked) {
            revealFab(MainActivity_FAB_NewTrip)
            revealFab(MainActivity_FAB_ReturnTrip)
            revealFab(MainActivity_FAB_Search)
        }
        else{
            hideFab(MainActivity_FAB_NewTrip)
            hideFab(MainActivity_FAB_ReturnTrip)
            hideFab(MainActivity_FAB_Search)
        }
        isAddClicked = isClicked
    }

    private fun revealFab(fab: FloatingActionButton) {
        val cx = fab.width / 2
        val cy = fab.height / 2
        val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim = createCircularReveal(fab, cx, cy, 0f, finalRadius)
        anim.duration = 250
        fab.visibility = View.VISIBLE
        anim.start()
    }

    private fun hideFab(fab: FloatingActionButton) {
        val cx = fab.width / 2
        val cy = fab.height / 2
        val initialRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()
        val anim = createCircularReveal(fab, cx, cy, initialRadius, 0f)
        anim.duration = 250
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                fab.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {}
        })
        anim.start()
    }

    private fun rotateFab(fab: FloatingActionButton) {
        val px = fab.width / 2.0f
        val py = fab.height / 2.0f
        val finialDegree = addFabRotateInitialDegree + 90
        val animation = RotateAnimation(addFabRotateInitialDegree, finialDegree, px, py)
        addFabRotateInitialDegree = finialDegree % 360
        animation.duration = 200
        animation.fillAfter = true
        fab.startAnimation(animation)
    }

    override fun onBackPressed() {
        when {
            isAddClicked -> {
                clickSound()
                addClick(false)
                isAddClicked = false
            }
            isSearch -> {
                clickSound()
                cancelSearch()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun clickSound() {
        val mediaPlayer = MediaPlayer.create(baseContext, R.raw.click_sound)
        mediaPlayer.start()
    }

    override fun onDoneClick(tripClass: Trip, isUpdate: Boolean) {
        clickSound()
        try {
            if (!isUpdate) {
                tripClassFragment!!.newTripIsAdded(tripClass,false)
            } else {
                cancelSearch()
                val message = if (tripClassFragment!!.updateTrip(tripClass)) {
                    resources.getString(R.string.ModifiedSuccessfully)
                } else {
                    resources.getString(R.string.Error)
                }
                Snackbar.make(
                    MainActivity_FrameLayout_FragmentContainer,
                    message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(baseContext, "An error occurred in app", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSettingClick(tripClass: Trip) {
        clickSound()
        val dialog = SettingTripFragment.newInstance(tripClass.getId())
        dialog.show(supportFragmentManager, null)
    }

    override fun updateClick(tripId: Int) {
        clickSound()
        try {
            val dialog = RecordTripFragment.newInstance(db.getTrip(tripId))
            dialog.show(supportFragmentManager, null)
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun deleteClick(tripId: Int) {
        clickSound()
        try {
            val message = if (tripClassFragment!!.deleteTrip(tripId)) {
                cancelSearch()
                resources.getString(R.string.DeleteSuccessfully)
            } else {
                resources.getString(R.string.Error)
            }
            Snackbar.make(
                MainActivity_FrameLayout_FragmentContainer,
                message,
                Snackbar.LENGTH_SHORT
            )
                .show()
        } catch (e: Exception) {
            Toast.makeText(baseContext, "An error occurred in app", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDateSelected(date: String) {
        clickSound()
        try {
            val pair = tripClassFragment!!.generatedSearchWithDate(date)     // return trips size and total cost
            isSearch = true
            MainActivity_ImageView_ArrowToReturn.visibility = View.VISIBLE
            notification(pair.first, pair.second)
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun notification(numberOfTrips: Int, totalDistance: Int) {
        val notificationManager = NotificationManagerCompat.from(baseContext)
        val notificationBuilder: NotificationCompat.Builder

        notificationBuilder = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            NotificationCompat.Builder(baseContext)
        } else {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
            NotificationCompat.Builder(baseContext, CHANNEL_ID)
        }

        val notificationBody =
            "${resources.getString(R.string.NOT_BODY1)}$numberOfTrips ${resources.getString(R.string.NOT_BODY2)}$totalDistance ${resources.getString(
                R.string.NOT_BODY3
            )}"
        notificationBuilder.setContentTitle(resources.getString(R.string.NOT_TITLE))
            .setContentText(notificationBody)
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + baseContext.packageName + "/" + R.raw.pop))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificationBody)
            )
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

    }

    override fun onNoteDoneClick(note: Note) {
        clickSound()
        try {
            note.getId()        // if id == -1  throw Exception that mean that make new note else mean that update this note
            val isUpdate = noteClassFragment!!.updateNote(note)
            if(isUpdate) Snackbar.make(MainActivity_FrameLayout_FragmentContainer,resources.getString(R.string.ModifiedSuccessfully),Snackbar.LENGTH_LONG).show()
            else Snackbar.make(MainActivity_FrameLayout_FragmentContainer,resources.getString(R.string.Error),Snackbar.LENGTH_LONG).show()
        } catch (e: NumberFormatException) {
            try {
                val isAdded = noteClassFragment!!.newNoteIsAdded(note)
                if(isAdded) Snackbar.make(MainActivity_FrameLayout_FragmentContainer,resources.getString(R.string.AddedSuccessfully),Snackbar.LENGTH_LONG).show()
                else  Snackbar.make(MainActivity_FrameLayout_FragmentContainer,resources.getString(R.string.Error),Snackbar.LENGTH_LONG).show()
            }catch (e:Exception){
                Snackbar.make(MainActivity_FrameLayout_FragmentContainer,resources.getString(R.string.Error),Snackbar.LENGTH_LONG).show()

            }
        }
    }

    override fun onNoteClickToViewOrUpdate(note: Note) {
        clickSound()
        val dialog = NoteClassWriteNoteFragment.newInstance(note)
        dialog.show(supportFragmentManager,null)
    }

    override fun onNoteLongClickToDelete(note: Note) {
        clickSound()
        val dialog = Dialog.newInstance(note)
        dialog.show(supportFragmentManager,null)
    }

    override fun onClickYes(note: Note) {
        clickSound()
        try {
            noteClassFragment!!.deleteNote(note)
            Snackbar.make(MainActivity_FrameLayout_FragmentContainer, resources.getString(R.string.DeleteSuccessfully), Snackbar.LENGTH_LONG).show()
        }catch (e:Exception){
            Snackbar.make(MainActivity_FrameLayout_FragmentContainer, resources.getString(R.string.Error), Snackbar.LENGTH_LONG).show()
        }

    }

}
