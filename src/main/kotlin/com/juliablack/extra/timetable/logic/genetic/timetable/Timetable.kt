package com.juliablack.extra.timetable.logic.genetic.timetable

import com.juliablack.extra.timetable.logic.genetic.timetable.enums.DayOfWeek
import com.juliablack.extra.timetable.util.getTime
import javafx.collections.ObservableList
import tornadofx.*
import kotlin.math.abs

/**
 * Расписание
 */
class Timetable {

    var timetable: List<GroupTimetable>

    constructor(timetable: List<GroupTimetable>) {
        this.timetable = timetable
    }

    constructor(individual: TimetableIndividual) {
        this.timetable = parseIndividualToTimeTable(individual)
    }

    private fun parseIndividualToTimeTable(individual: TimetableIndividual): List<GroupTimetable> {
        val timetable = mutableListOf<GroupTimetable>()
        individual.groups ?: throw Exception("В расписание не передан список групп")
        individual.groups?.forEach { group ->
            val list = mutableListOf<DayClass>()
            individual.getClasses().forEachIndexed { index, studentClass ->
                if (studentClass.group == group) {
                    val time = individual.getTimes().getGen(index) as Time
                    val room = individual.getRooms().getGen(index) as ClassRoom

                    if (list.find { it.dayOfWeek == time.dayOfWeek } == null) { //если нет такого дня недели еще
                        list.add(
                                DayClass(
                                        time.dayOfWeek, mutableListOf()
                                ))
                    }
                    list.find { it.dayOfWeek == time.dayOfWeek }?.classes?.add(
                            SimpleClass(
                                    time.numberClass,
                                    studentClass.teacher.name,
                                    studentClass.lesson.name,
                                    studentClass.lesson.typeLesson.toString(),
                                    room.number,
                                    room.building
                            ))
                    list.forEach {
                        it.classes.sortBy { studentClass -> studentClass.time }
                    }
                }
                list.sortBy { it.dayOfWeek }
            }
            timetable.add(GroupTimetable(group = group, list = list))
        }
        return timetable
    }


    fun parseTimetableToView(groupNumber: String?): Pair<ObservableList<GroupTimetableForView>?, String?> {
        this.timetable.find { it.group.number == groupNumber || groupNumber.isNullOrBlank()}?.apply {
            val timetableForView = observableList<GroupTimetableForView>()
            this.list.forEach { dayClass ->
                dayClass.classes.forEachIndexed { index, simpleClass ->
                    if (timetableForView.size <= index) {
                        timetableForView.add(GroupTimetableForView())
                    }
                    timetableForView[index].time = index.getTime()
                    when (dayClass.dayOfWeek) {
                        DayOfWeek.MONDAY -> {
                            timetableForView[index].monday = simpleClass.toString()
                        }
                        DayOfWeek.TUESDAY -> {
                            timetableForView[index].tuesday = simpleClass.toString()
                        }
                        DayOfWeek.WEDNESDAY -> {
                            timetableForView[index].wednesday = simpleClass.toString()
                        }
                        DayOfWeek.THURSDAY -> {
                            timetableForView[index].thursday = simpleClass.toString()
                        }
                        DayOfWeek.FRIDAY -> {
                            timetableForView[index].friday = simpleClass.toString()
                        }
                        DayOfWeek.SATURDAY -> {
                            timetableForView[index].saturday = simpleClass.toString()
                        }
                    }
                }
            }
            return Pair(timetableForView, group.number)
        }
        return Pair(null, groupNumber)
    }

    fun getCountInterval(): Int {
        var count = 0
        this.timetable.forEach {
            it.list.forEach { day ->
                day.classes.forEachIndexed { index, simpleClass ->
                    if (index > 0 && !isNextClass(day.classes[index - 1], simpleClass)) {
                        count++
                    }
                }
            }
        }
        return count
    }

    private fun isNextClass(simpleClass1: SimpleClass, simpleClass2: SimpleClass): Boolean {
        return if (simpleClass1.building == simpleClass2.building) {
            abs(simpleClass1.time - simpleClass2.time) == 1
        } else {
            abs(simpleClass1.time - simpleClass2.time) > 1
        }
    }

}