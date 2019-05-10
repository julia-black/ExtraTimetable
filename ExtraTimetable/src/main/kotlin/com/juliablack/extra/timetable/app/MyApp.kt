package com.juliablack.extra.timetable.app

import com.juliablack.extra.timetable.logic.GeneratorTimeTable
import com.juliablack.extra.timetable.view.MainView
import tornadofx.*


class MyApp : App(MainView::class, Styles::class) {

    init {
        val generatorTimeTable = GeneratorTimeTable()
        generatorTimeTable.generateStartPopulation(COUNT_OF_POPULATION)
        generatorTimeTable.generateTimetable()
    }

    companion object {
        const val COUNT_OF_POPULATION = 1
    }
}