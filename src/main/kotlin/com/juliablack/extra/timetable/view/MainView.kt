package com.juliablack.extra.timetable.view

import com.github.thomasnield.rxkotlinfx.actionEvents
import com.juliablack.extra.timetable.controller.EventController
import com.juliablack.extra.timetable.logic.genetic.GeneratorTimetable
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonType.OK
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.*


class MainView : View() {

    override val root = BorderPane()

    private val controller: EventController by inject()

    init {
        title = "ExtraTimeTable"

        with(root) {
            setPrefSize(940.0, 610.0)
            top = menubar {
                menu("Расписание") {
                    item("Сгенерировать").apply {
                        actionEvents()
                                .map { Unit }
                                .subscribe(controller.generateTimetable)
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
        subscribeAllEvent()
    }

    private fun subscribeAllEvent() {
        controller.generateTimetable
                .subscribe {
                    runAsync {

                        updateTitle("Загрузка данных")

                        val generatorTimeTable = GeneratorTimetable(OPTIONAL_LESSONS_OF_DAY, MAX_LESSONS_OF_DAY)

                       // Thread.sleep(200)
                        updateProgress(1, 3)
                        updateTitle("Генерация стартовой популяции")

                        generatorTimeTable.generateStartPopulation(COUNT_OF_POPULATION)

                        //Thread.sleep(200)
                        updateProgress(2, 3)
                        updateTitle("Генерация расписания.")

                        generatorTimeTable.generateTimetable()

                                // Thread.sleep(200)
                        updateProgress(3, 3)
                    }
                }
    }

    companion object {
        const val COUNT_OF_POPULATION = 100
        const val MAX_LESSONS_OF_DAY = 6 //максимальное количество пар в день
        const val OPTIONAL_LESSONS_OF_DAY = 4 //желательное количество пар в день
    }
}