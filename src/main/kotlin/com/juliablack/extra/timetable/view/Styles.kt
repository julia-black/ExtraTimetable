package com.juliablack.extra.timetable.view

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val settings by cssclass()
    }

    init {
        settings {
            padding = box(16.px)
        }
    }
}