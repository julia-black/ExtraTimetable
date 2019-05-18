package com.juliablack.extra.timetable.view

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.juliablack.extra.timetable.controller.EventController
import com.juliablack.extra.timetable.logic.GeneratorTimetable
import javafx.geometry.Orientation
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.Stage
import tornadofx.*


class MainView : View() {

    override val root = BorderPane()

   // private val controller: EventController by inject()

    init {
        title = "ExtraTimeTable"

        with(root) {
            setPrefSize(940.0, 610.0)
            top = menubar {
                menu("Расписание") {
                    item("Сгенерировать").apply {
                        actionEvents().map { Unit }.subscribe {
                            action {
                                runAsync {
                                    updateTitle("Загрузка данных")
                                    for (i in 1..10) {
                                        updateMessage("$i...")
                                        if (i == 5)
                                            updateTitle("Генерация")
                                        clickedGenerateTimetable()
                                        Thread.sleep(200)
                                        updateProgress(i.toLong(), 10)
                                    }
                                }
                            }

                        }
                    }

                    item("Alert").apply {
                        actionEvents().subscribe {
                            Alert(AlertType.INFORMATION, "", OK).apply {
                                val stage = dialogPane.scene.window as Stage
                                stage.icons.add(Image("/app/timetable.png"))
                                stage.showAndWait()
                            }
                        }
                    }
                }
                menu("Настройки") {
                }
            }
            bottom {
                add<ProgressView>()
            }
        }
    }

    private fun clickedGenerateTimetable() {

        val generatorTimeTable = GeneratorTimetable()
        generatorTimeTable.maxLessonsOfDay = MAX_LESSONS_OF_DAY
        generatorTimeTable.optionalLessonsOfDay = OPTIONAL_LESSONS_OF_DAY
        generatorTimeTable.generateStartPopulation(COUNT_OF_POPULATION)
        generatorTimeTable.generateTimetable()
    }

    class ProgressView : View() {
        val status: TaskStatus by inject()

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

    companion object {
        const val COUNT_OF_POPULATION = 1
        const val MAX_LESSONS_OF_DAY = 6 //максимальное количество пар в день
        const val OPTIONAL_LESSONS_OF_DAY = 3 //желательное количество пар в день
    }
}