package com.example.testamura

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.edt_agenda
import kotlinx.android.synthetic.main.activity_main.edt_participants
import kotlinx.android.synthetic.main.activity_main.edt_title
import kotlinx.android.synthetic.main.activity_main.text_date
import java.util.*

class UpdateEventActivity : AppCompatActivity() {

    var event: Event? = null
    var databaseEventReference: DatabaseReference? = null
    var auth: FirebaseAuth? = null
    val myCalendar = Calendar.getInstance()
    var btnSubmit: Button? = null
    var selectedDate = ""
    var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var month = monthOfYear
            month++
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate = "$dayOfMonth-$month-$year"
            text_date.text = "$dayOfMonth-$month-$year"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_event)

        var bundle = intent.extras
        event = Event(
            bundle?.getString("uId").toString()
            , bundle?.getString("title").toString()
            , bundle?.getString("agenda").toString()
            , bundle?.getString("participants").toString()
            , bundle?.getString("date").toString()
        )
        selectedDate = event?.date ?: ""

        event?.setEventId(bundle?.getString("id").toString())

        event?.let {
            edt_title?.setText(it.title)
            edt_agenda?.setText(it.agenda)
            edt_participants?.setText(it.participants)
            text_date?.text = it.date
        }

        btnSubmit = findViewById(R.id.btn_submit)
        auth = FirebaseAuth.getInstance()
        databaseEventReference = Utils.getDatabase().getReference("events")


        btnSubmit?.setOnClickListener {
            editEvent()
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


    fun showDatePickerDialog(v: View) {
        hideKeyboard(this)
        DatePickerDialog(
            this@UpdateEventActivity, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun editEvent() {
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

            val eventData = Event(auth?.uid + "", title, agenda, participants, date)

            databaseEventReference?.child(event?.id ?: "")?.setValue(eventData)

            //setting edittext to blank again
            edt_title.setText("")
            edt_agenda.setText("")
            edt_participants.setText("")
            text_date.text = "Date"

            //displaying a success toast
            Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show()
            finish()
        } else {
            //if the value is not given displaying a toast
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_LONG).show()
        }
    }

}
