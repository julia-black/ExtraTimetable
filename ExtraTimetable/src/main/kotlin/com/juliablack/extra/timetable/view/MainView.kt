package com.juliablack.extra.timetable.view

import com.juliablack.extra.timetable.app.Styles
import tornadofx.*

class MainView : View("ExtraTimetable") {
    override val root = hbox {
        label("ExtraTimetable - удобное составление расписания занятий для ВУЗов") {
            addClass(Styles.heading)
        }
    }
}