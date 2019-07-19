package com.example.testamura

class Event {
    var id: String = ""
    var uId: String = ""
    var title: String = ""
    var agenda: String = ""
    var participants: String = ""
    var date: String = ""

    constructor() {
    }

    constructor(uId: String, title: String, agenda: String, participants: String, date: String) {
        this.uId = uId
        this.title = title
        this.agenda = agenda
        this.participants = participants
        this.date = date

    }

    fun setEventId(id:String)
    {
        this.id=id
    }
}