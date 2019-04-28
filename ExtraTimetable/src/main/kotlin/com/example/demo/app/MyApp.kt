package com.example.demo.app

import com.example.demo.logic.GeneratorTimeTable
import com.example.demo.view.MainView
import tornadofx.*


class MyApp: App(MainView::class, Styles::class) {

    val timeTable = GeneratorTimeTable()
}