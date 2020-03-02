package com.juliablack.extra.timetable.view

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.juliablack.extra.timetable.controller.EventController
import com.juliablack.extra.timetable.logic.genetic.GeneratorTimetable
import com.juliablack.extra.timetable.logic.genetic.timetable.Const.COUNT
import com.juliablack.extra.timetable.logic.genetic.timetable.Const.COUNT_CYCLE_ALGORITHM
import com.juliablack.extra.timetable.logic.genetic.timetable.Const.COUNT_OF_POPULATION
import com.juliablack.extra.timetable.logic.genetic.timetable.Const.MAX_LESSONS_OF_DAY
import com.juliablack.extra.timetable.logic.genetic.timetable.Const.OPTIONAL_LESSONS_OF_DAY
import com.juliablack.extra.timetable.util.Util
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*

class MainView : View() {

    override val root = BorderPane()

    private val controller: EventController by inject()

    init {
        title = "ExtraTimeTable"
        showTopBar()
        subscribeAllEvent()
    }

    private fun showTopBar() {
        with(root) {
            setPrefSize(940.0, 610.0)
            top {
                this += menubar {
                    menu("Импорт") {
                        item("Из Excel-файла").apply {
                            actionEvents()
                                    .map { Unit }
                                    .subscribe(controller.showViewOpenFile)
                        }
                    }
                    menu("Сгенерировать") {
                        item("Из внутренней базы данных").apply {
                            actionEvents()
                                    .map { Unit }
                                    .subscribe(controller.generateTimetable)
                        }
                    }
                    menu("Настройки") {
                    }
                }
            }
            center {
                this += borderpane {
                    center {
                        imageview("/app/import_file.png") {
                            fitWidth = 390.0
                            fitHeight = 250.0
                        }
                    }
                }
            }
            bottom {
                add<ProgressView>()
            }
        }
    }

    private fun subscribeAllEvent() {
        controller.generateTimetable
                .subscribe {
                    runAsync {

                        updateTitle("Загрузка данных")

                        try {
                            val generatorTimeTable = GeneratorTimetable(OPTIONAL_LESSONS_OF_DAY, MAX_LESSONS_OF_DAY)
                            val allProgress = (COUNT_OF_POPULATION + COUNT_CYCLE_ALGORITHM) * COUNT // todo временно * COUNT
                            var progress = 0L

                            val results = mutableListOf<Triple<Float, Float, Float>>()
                            for (i in 0 until COUNT) {
                                generatorTimeTable.generateStartPopulation(COUNT_OF_POPULATION) {
                                    updateProgress(progress++, allProgress)
                                }
                                updateTitle("Генерация расписания")
                                generatorTimeTable.testExperiment {
                                    updateProgress(progress++, COUNT_CYCLE_ALGORITHM)
                                }.subscribe {
                                    val timetable = it.first
                                    val result = it.second
                                    results.add(result)
                                    generatorTimeTable.saveTimetable(timetable)
                                    Platform.runLater {
                                        Alert(AlertType.INFORMATION, "Расписание timetable.json создано в папке проекта", OK).apply {
                                            val stage = dialogPane.scene.window as Stage
                                            stage.icons.add(Image("/app/timetable.png"))
                                            stage.showAndWait()
                                        }
                                    }
                                    if (results.size == COUNT) {
                                        Util.showResult(results)
                                    }
                                }
                            }
                            //todo test
//                            updateTitle("Генерация стартовой популяции")
//                            generatorTimeTable.generateStartPopulation(COUNT_OF_POPULATION) {
//                                updateProgress(progress++, allProgress)
//                            }
//                            updateTitle("Генерация расписания")
//                            generatorTimeTable.generateTimetable {
//                                updateProgress(progress++, COUNT_CYCLE_ALGORITHM)
//                            }.subscribe {
//                                generatorTimeTable.saveTimetable(it)
//                                Platform.runLater {
//                                    Alert(AlertType.INFORMATION, "Расписание timetable.json создано в папке проекта", OK).apply {
//                                        val stage = dialogPane.scene.window as Stage
//                                        stage.icons.add(Image("/app/timetable.png"))
//                                        stage.showAndWait()
//                                    }
//                                }
//                            }
                        } catch (e: Exception) {
                            Platform.runLater {
                                Alert(AlertType.ERROR, e.message, OK).apply {
                                    val stage = dialogPane.scene.window as Stage
                                    stage.icons.add(Image("/app/timetable.png"))
                                    stage.showAndWait()
                                }
                            }

                        }
                    }
                }

        controller.showViewOpenFile
                .subscribe {
                    val filters = arrayOf(FileChooser.ExtensionFilter("Файлы Excel", "*.xls", "*.xlsx"))
                    val files = chooseFile("Выберите файл", filters)
                    if (files.isNotEmpty()) {
                        runAsync {
                            updateTitle("Загрузка данных")
                            val generatorTimeTable = GeneratorTimetable(OPTIONAL_LESSONS_OF_DAY, MAX_LESSONS_OF_DAY, files[0])
                            val allProgress = (COUNT_OF_POPULATION + COUNT_CYCLE_ALGORITHM) * COUNT //todo временно * COUNT
                            var progress = 0L

                            val results = mutableListOf<Triple<Float, Float, Float>>()
                            for (i in 0 until COUNT) {
                                updateTitle("Генерация стартовой популяции")
                                generatorTimeTable.generateStartPopulation(COUNT_OF_POPULATION) {
                                    updateProgress(progress++, allProgress)
                                }
                                updateTitle("Генерация расписания")
                                generatorTimeTable.testExperiment {
                                    updateProgress(progress++, allProgress)
                                }.subscribe {
                                    val timetable = it.first
                                    results.add(it.second)
                                    generatorTimeTable.saveTimetable(timetable)
                                    Platform.runLater {
                                        Alert(AlertType.INFORMATION, "Расписание timetable.json создано в папке проекта", OK).apply {
                                            val stage = dialogPane.scene.window as Stage
                                            stage.icons.add(Image("/app/timetable.png"))
                                            stage.showAndWait()
                                        }
                                    }
                                    if (results.size == COUNT) {
                                        Util.showResult(results)
                                    }
                                }
                            }

                            //todo test
//                            updateTitle("Генерация стартовой популяции")
//                            generatorTimeTable.generateStartPopulation(COUNT_OF_POPULATION) {
//                                updateProgress(progress++, allProgress)
//                            }
//
//                            updateTitle("Генерация расписания")
//                            generatorTimeTable.generateTimetable {
//                                updateProgress(progress++, allProgress)
//                            }.subscribe {
//                                generatorTimeTable.saveTimetable(it)
//                                Platform.runLater {
//                                    Alert(AlertType.INFORMATION, "Расписание timetable.json создано в папке проекта", OK).apply {
//                                        val stage = dialogPane.scene.window as Stage
//                                        stage.icons.add(Image("/app/timetable.png"))
//                                        stage.showAndWait()
//                                    }
//                                }
//                            }
                        }
                    }

                }
    }
}