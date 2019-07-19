package com.example.testamura

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.example.testamura.adapter.EventListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class UsersEventsActivity : AppCompatActivity(), OnClickListener {
    override fun onClick(position: Int, event: Event) {
        databaseEventReference?.child(event.id)?.removeValue()
    }

    var databaseEventReference: DatabaseReference? = null
    var auth: FirebaseAuth? = null
    var listEvents: ListView? = null
    val events = ArrayList<Event>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_events)

        auth = FirebaseAuth.getInstance()
        listEvents = findViewById(R.id.lst_events)
        databaseEventReference = Utils.getDatabase().getReference("events");

        val queryRef = databaseEventReference?.orderByChild("uid")?.equalTo(auth?.uid)
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
                var adapter = EventListAdapter(this@UsersEventsActivity, events, this@UsersEventsActivity)
                listEvents?.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}
