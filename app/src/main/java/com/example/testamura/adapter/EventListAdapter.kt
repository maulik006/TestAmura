package com.example.testamura.adapter

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.testamura.Event
import com.example.testamura.OnClickListener
import com.example.testamura.R
import com.example.testamura.UpdateEventActivity

class EventListAdapter(var context: Activity, var events: List<Event>, var listener: OnClickListener) :
    ArrayAdapter<Event>(context, R.layout.layout_event, events) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getLayoutInflater()
        val listViewItem = inflater.inflate(R.layout.layout_event, null, true)

        val textTitle = listViewItem.findViewById(R.id.text_view_title) as TextView
        val textAgenda = listViewItem.findViewById(R.id.text_view_agenda) as TextView
        val textParticipants = listViewItem.findViewById(R.id.text_view_participants) as TextView
        val textDate = listViewItem.findViewById(R.id.text_view_date) as TextView
        val txtViewDelete = listViewItem.findViewById(R.id.text_view_delete) as TextView
        val llEvent = listViewItem.findViewById(R.id.ll_event) as LinearLayout

        val event = events.get(position)
        textTitle.setText(event.title)
        textAgenda.setText(event.agenda)
        textParticipants.setText(event.participants)
        textDate.setText(event.date)
        txtViewDelete.setOnClickListener {
            listener.onClick(position, event)
        }
        llEvent.setOnClickListener {
            var intent = Intent(context, UpdateEventActivity::class.java)
            intent.putExtra("id", event.id)
            intent.putExtra("title", event.title)
            intent.putExtra("agenda", event.agenda)
            intent.putExtra("participants", event.participants)
            intent.putExtra("date", event.date)
            intent.putExtra("uId", event.uId)
            context.startActivity(intent)
        }
        return listViewItem
    }
}