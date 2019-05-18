package com.juliablack.extra.timetable.controller

import io.reactivex.subjects.BehaviorSubject
import tornadofx.*

class EventController : Controller() {

    val generateTimetable = BehaviorSubject.create<Unit>()
}