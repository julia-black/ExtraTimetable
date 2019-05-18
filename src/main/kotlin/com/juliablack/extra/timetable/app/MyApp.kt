package com.juliablack.extra.timetable.app

import com.juliablack.extra.timetable.view.MainView
import javafx.scene.image.Image

import tornadofx.*

class MyApp: App(MainView::class, Styles::class) {
    init {
        addStageIcon(Image("/app/timetable.png"))
    }
}