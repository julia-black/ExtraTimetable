package com.juliablack.extra.timetable.view

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

class ProgressView : View() {
    private val status: TaskStatus by inject()

    override val root = vbox(4) {
        visibleWhen { status.running }
        style { borderColor += box(Color.LIGHTGREY, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT) }
        label(status.title).style { fontWeight = FontWeight.BOLD }
        hbox(4) {
            label(status.message)
            progressbar(status.progress)
            visibleWhen { status.running }
        }
    }
}