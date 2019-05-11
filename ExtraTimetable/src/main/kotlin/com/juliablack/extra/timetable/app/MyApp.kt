package com.juliablack.extra.timetable.app

import com.juliablack.extra.timetable.logic.GeneratorTimeTable
import com.juliablack.extra.timetable.view.MainView
import javafx.scene.image.Image
import tornadofx.*
import java.io.File

class MyApp : App(MainView::class, Styles::class) {

    init {
       addStageIcon(Image(File("src/main/resources/app/timetable.png").toURI().toString()))

       val generatorTimeTable = GeneratorTimeTable()
       generatorTimeTable.maxLessonsOfDay = MAX_LESSONS_OF_DAY
       generatorTimeTable.optionalLessonsOfDay = OPTIONAL_LESSONS_OF_DAY
       generatorTimeTable.generateStartPopulation(COUNT_OF_POPULATION)
       generatorTimeTable.generateTimetable()
    }



    companion object {
        const val COUNT_OF_POPULATION = 1
        const val MAX_LESSONS_OF_DAY = 6 //максимальное количество пар в день
        const val OPTIONAL_LESSONS_OF_DAY = 3 //желательное количество пар в день
    }
}