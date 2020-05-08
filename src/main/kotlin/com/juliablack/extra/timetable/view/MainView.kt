package com.juliablack.extra.timetable.view

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.juliablack.extra.timetable.app.Settings
import com.juliablack.extra.timetable.controller.EventController
import com.juliablack.extra.timetable.logic.genetic.GeneratorTimetable
import com.juliablack.extra.timetable.logic.genetic.timetable.GroupTimetableForView
import com.juliablack.extra.timetable.logic.genetic.timetable.Timetable
import com.juliablack.extra.timetable.util.Util
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import tornadofx.*
import java.time.LocalDate
import java.time.Period

class Person(val id: Int, val name: String, val birthday: LocalDate) {
    val age: Int get() = Period.between(birthday, LocalDate.now()).years
}

class MainView : View() {

    override val root = BorderPane()

    private val controller: EventController by inject()

    private var imageView: ImageView? = null
    //private var tableView: TableView<GroupTimetableForView>? = null

    private var borderPane: BorderPane = borderpane {
        center {
            imageView = imageview("/app/import_file.png") {
                fitWidth = 390.0
                fitHeight = 250.0
            }
        }
    }

    private val persons = observableList(
            Person(1, "Samantha Stuart", LocalDate.of(1981, 12, 4)),
            Person(2, "Tom Marks", LocalDate.of(2001, 1, 23)),
            Person(3, "Stuart Gills", LocalDate.of(1989, 5, 23)),
            Person(3, "Nicole Williams", LocalDate.of(1998, 8, 11))
    )

    init {
        title = "ExtraTimeTable"
        showView()
        subscribeAllEvent()
    }

    private fun showView() {
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
                        item("Изменить").apply {
                            actionEvents()
                                    .map { Unit }
                                    .subscribe(controller.openSettings)
                        }
                    }
                }
            }
            center {
                this += borderPane
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
                            val generatorTimeTable = GeneratorTimetable(Settings.optimalLessonsOfDay,
                                    Settings.maxLessonsOfDay - 1)
                            val allProgress = (Settings.countOfPopulation + Settings.countCycle) * Settings.count
                            var progress = 0L

                            val results = mutableListOf<Triple<Float, Float, Float>>()
                            for (i in 0 until Settings.count) {
                                generatorTimeTable.generateStartPopulation(Settings.countOfPopulation) {
                                    updateProgress(progress++, allProgress)
                                }
                                updateTitle("Генерация расписания")
                                generatorTimeTable.testExperiment {
                                    updateProgress(progress++, Settings.countCycle)
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
                                    if (results.size == Settings.count) {
                                        Util.showResult(results)
                                    }
                                }
                            }
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
                            val generatorTimeTable = GeneratorTimetable(
                                    Settings.optimalLessonsOfDay,
                                    Settings.maxLessonsOfDay - 1,
                                    files[0]
                            )
                            val allProgress = (Settings.countOfPopulation + Settings.countCycle) * Settings.count
                            var progress = 0L

                            val results = mutableListOf<Triple<Float, Float, Float>>()
                            for (i in 0 until Settings.count) {
                                updateTitle("Генерация стартовой популяции")
                                generatorTimeTable.generateStartPopulation(Settings.countOfPopulation) {
                                    updateProgress(progress++, allProgress)
                                }
                                updateTitle("Генерация расписания")
                                generatorTimeTable.testExperiment {
                                    updateProgress(progress++, allProgress)
                                }.subscribe {
                                    val timetable = it.first
                                    results.add(it.second)
                                    generatorTimeTable.saveTimetable(timetable)
                                    showTimetable(timetable, "111")
                                    if (results.size == Settings.count) {
                                        Util.showResult(results)
                                    }
                                }
                            }
                        }
                    }
                }

        controller.openSettings
                .subscribe {
                    val newScope = Scope()
                    find<SettingsView>(newScope).openWindow(owner = null)
                }
    }

    private fun showTimetable(timetable: Timetable, groupNumber: String) {
        val timetableForView = timetable.parseTimetableToView(groupNumber)
        if (timetableForView == null) {
            warning("Не найдена введенная группа")
        } else {
            borderPane = borderpane {
                center {
                    tableview(timetableForView) {
                        readonlyColumn("", GroupTimetableForView::time)
                        readonlyColumn("Понедельник", GroupTimetableForView::monday)
                        readonlyColumn("Вторник", GroupTimetableForView::tuesday)
                        readonlyColumn("Среда", GroupTimetableForView::wednesday)
                        readonlyColumn("Четверг", GroupTimetableForView::thursday)
                        readonlyColumn("Пятница", GroupTimetableForView::friday)
                        readonlyColumn("Суббота", GroupTimetableForView::saturday)
                    }.columns.forEach {
                        it.isSortable = false
                    }
                }
            }
        }
        Platform.runLater {
            showView()
        }
    }
}