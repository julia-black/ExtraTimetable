package com.juliablack.extra.timetable.app

import com.juliablack.extra.timetable.view.MainView
import javafx.scene.image.Image
import javafx.stage.Stage
import tornadofx.*

class MyApp : App(MainView::class, Styles::class) {
    init {
        addStageIcon(Image("/app/timetable.png"))
    }

//    companion object {
//        @JvmStatic
//        fun main(args: Array<String>) {
//            launch(*args)
//        }
//    }
//
//    override fun start(stage: Stage) {
//        addStageIcon(Image("/app/timetable.png"))
//    }
}