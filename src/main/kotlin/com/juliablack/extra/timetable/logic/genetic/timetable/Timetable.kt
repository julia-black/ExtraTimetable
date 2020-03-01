package com.juliablack.extra.timetable.logic.genetic.timetable

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