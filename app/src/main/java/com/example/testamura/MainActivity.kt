package com.example.testamura

import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.widget.Toast
import android.text.TextUtils
import android.view.View
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.app.AppCompatActivity
import com.example.testamura.adapter.EventListAdapter
import com.google.firebase.database.*
import android.app.DatePickerDialog
import java.util.*
import kotlin.collections.ArrayList
import android.app.Activity
import android.content.DialogInterface
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity(), OnClickListener {

    var btnSignOut: Button? = null
    var auth: FirebaseAuth? = null
    var databaseEventReference: DatabaseReference? = null
    var listEvents: ListView? = null
    val events = ArrayList<Event>()
    val myCalendar = Calendar.getInstance()
    var selectedDate = ""
    var isFilter = false
    var builder: AlertDialog.Builder? = null

    var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var month = monthOfYear
            month++
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            if (isFilter) {
                isFilter = false
                filterData("$dayOfMonth-$month-$year")
            } else {
                selectedDate = "$dayOfMonth-$month-$year"
                text_date.text = "$dayOfMonth-$month-$year"
            }
        }

    override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()
        builder = AlertDialog.Builder(this)
        databaseEventReference = Utils.getDatabase().getReference("events")

        var queryRef = databaseEventReference?.orderByChild("uid")?.equalTo(auth?.uid)
        queryRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //clearing the previous artist list
                events.clear()
                //iterating through all the nodes
                for (postSnapshot in dataSnapshot.children) {
                    try {
                        //val note = dataSnapshot.getValue(Event::class.java)
                        val note = Event(
                            postSnapshot.child("id").value.toString(),
                            postSnapshot.child("title").value.toString(),
                            postSnapshot.child("agenda").value.toString(),
                            postSnapshot.child("participants").value.toString(),
                            postSnapshot.child("date").value.toString()
                        )
                        note.setEventId(postSnapshot.key.toString())
                        events.add(note)

                    } catch (e: Exception) {
                        e.stackTrace
                    }
                }
                //creating adapter
                var adapter = EventListAdapter(this@MainActivity, events, this@MainActivity)
                listEvents?.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_filter -> {
            showDatePickerDialog(null)
            true
        }

        R.id.action_all_data -> {
            filterData("")
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)

        val actionFilter = menu?.findItem(R.id.action_filter)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mTopToolbar = findViewById<Toolbar>(R.id.my_toolbar)
        setSupportActionBar(mTopToolbar)

        btnSignOut = findViewById(R.id.btn_signout)

        listEvents = findViewById(R.id.lst_events)
        btnSignOut?.setOnClickListener {
            auth?.signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        btn_submit?.setOnClickListener {
            addEvent()
        }
    }

    private fun hideKeyboard(activity: Activity) {
        try {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
        }

    }

    fun showDatePickerDialog(v: View?) {
        if (v == null) {
            isFilter = true
        }
        hideKeyboard(this)
        val dpd = DatePickerDialog(
            this@MainActivity, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        if (v != null)
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
        dpd.show()
    }

    private fun filterData(date: String) {
        var queryRef = databaseEventReference?.orderByChild("uid")?.equalTo(auth?.uid)
        if (date.isNotEmpty())
            queryRef = databaseEventReference?.orderByChild("date")?.equalTo(date)

        queryRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //clearing the previous artist list
                events.clear()
                //iterating through all the nodes
                for (postSnapshot in dataSnapshot.children) {
                    try {
                        //val note = dataSnapshot.getValue(Event::class.java)
                        val note = Event(
                            postSnapshot.child("id").value.toString(),
                            postSnapshot.child("title").value.toString(),
                            postSnapshot.child("agenda").value.toString(),
                            postSnapshot.child("participants").value.toString(),
                            postSnapshot.child("date").value.toString()
                        )
                        note.setEventId(postSnapshot.key.toString())
                        events.add(note)

                    } catch (e: Exception) {
                        e.stackTrace
                    }
                }
                //creating adapter
                var adapter = EventListAdapter(this@MainActivity, events, this@MainActivity)
                listEvents?.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    private fun addEvent() {
        //getting the values to save
        val title = edt_title?.text?.toString()?.trim() ?: ""
        val agenda = edt_agenda?.text?.toString()?.trim() ?: ""
        val participants = edt_participants?.text?.toString()?.trim() ?: ""
        val date = selectedDate
        //checking if the value is provided
        if (!TextUtils.isEmpty(title) &&
            !TextUtils.isEmpty(agenda) &&
            !TextUtils.isEmpty(participants) &&
            !TextUtils.isEmpty(date)
        ) {
            hideKeyboard(this)
            val id = databaseEventReference?.push()?.key

            val event = Event(auth?.uid + "", title, agenda, participants, date)

            databaseEventReference?.child(id ?: "")?.setValue(event)

            //setting edittext to blank again
            edt_title.setText("")
            edt_agenda.setText("")
            edt_participants.setText("")
            text_date.text = "Date"

            //displaying a success toast
            Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show()
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick(position: Int, event: Event) {
        val onPositive =
            DialogInterface.OnClickListener { _, _ -> databaseEventReference?.child(event.id)?.removeValue() }
        val onNegative =
            DialogInterface.OnClickListener { _, _ -> }
        builder?.setMessage("Are you sure you want to delete?")?.setPositiveButton("Yes", onPositive)
            ?.setNegativeButton("No", onNegative)?.show()
    }
}
