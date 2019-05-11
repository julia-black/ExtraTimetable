package com.juliablack.extra.timetable.app

import com.juliablack.extra.timetable.logic.GeneratorTimeTable
import com.juliablack.extra.timetable.view.MainView
import tornadofx.*


class MyApp : App(MainView::class, Styles::class) {

    init {
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