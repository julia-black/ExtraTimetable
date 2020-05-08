package com.juliablack.extra.timetable.view

import com.juliablack.extra.timetable.app.Settings
import com.juliablack.extra.timetable.app.Styles
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import tornadofx.*

class SettingsView : View("Настройки") {

    private var countCycleTextField: TextField by singleAssign()
    private var countOfPopulationTextField: TextField by singleAssign()
    private var maxLessonsTextField: TextField by singleAssign()
    private var optimalLessonsTextField: TextField by singleAssign()
    private var probablyMutationTextField: TextField by singleAssign()
    private var countTextField: TextField by singleAssign()
    private var firstSemesterCheckbox: CheckBox by singleAssign()

    override val root = vbox {
        setPrefSize(500.0, 300.0)
        addClass(Styles.settings)
        form {
            fieldset {
                field("Оптимальное количество пар в день:") {
                    optimalLessonsTextField = textfield(Settings.optimalLessonsOfDay.toString())
                }
                field("Максимальное количество пар в день:") {
                    maxLessonsTextField = textfield(Settings.maxLessonsOfDay.toString())
                }
                field("Количество особей в популяции:") {
                    countOfPopulationTextField = textfield(Settings.countOfPopulation.toString())
                }
                field("Количество повторений:") {
                    countTextField = textfield(Settings.count.toString())
                }
                field("Количество циклов генетического алгоритма:") {
                    countCycleTextField = textfield(Settings.countCycle.toString())
                }
                field("Вероятность мутации:") {
                    probablyMutationTextField = textfield(Settings.probabilityMutation.toString())
                }
                firstSemesterCheckbox = checkbox("Составить расписание на первое полугодие?").apply {
                    isSelected = Settings.isFirstSemester
                }

            }
            button("Сохранить").action {
                saveSettings()
            }
        }
    }

    private fun saveSettings() {
        try {
            Settings.optimalLessonsOfDay = optimalLessonsTextField.text.toInt()
            Settings.maxLessonsOfDay = maxLessonsTextField.text.toInt()
            Settings.countOfPopulation = countOfPopulationTextField.text.toInt()
            Settings.count = countTextField.text.toInt()
            Settings.probabilityMutation = probablyMutationTextField.text.toDouble()
            Settings.countCycle = countCycleTextField.text.toLong()
            Settings.isFirstSemester = firstSemesterCheckbox.isSelected
        } catch (e: Exception) {
            warning("Неверный формат данных", "Проверьте значения и попробуйте еще раз")
        }
        close()
    }
}