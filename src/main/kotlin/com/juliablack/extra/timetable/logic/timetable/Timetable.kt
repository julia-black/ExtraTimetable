package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.timetable.enums.DayOfWeek


/**
 * Расписание
 */
class Timetable {

    private var timetable: List<GroupTimetable>

    constructor(timetable: List<GroupTimetable>) {
        this.timetable = timetable
    }

    constructor(individual: TimetableIndividual) {
        this.timetable = parseIndividualToTimeTable(individual)
    }

    private fun parseIndividualToTimeTable(individual: TimetableIndividual): List<GroupTimetable> {
        val list = mutableListOf<GroupTimetable>()
        individual.groups ?: throw Exception("В расписание не передан список групп")
        individual.groups!!.forEach { group ->
            val map: MutableMap<DayOfWeek, MutableList<Triple<Int, ClassRoom, StudentClass>>> = mutableMapOf()
            individual.getClasses().forEachIndexed { index, studentClass ->
                if (studentClass.group == group) {
                    val time = individual.getTimes().getGen(index) as Time
                    val room = individual.getRooms().getGen(index) as ClassRoom

                    if (map[time.dayOfWeek] == null)
                        map[time.dayOfWeek] = mutableListOf()
                    map[time.dayOfWeek]!!.add(Triple(time.numberClass, room, studentClass))
                }
            }
            list.add(GroupTimetable(group = group, timetable = map))
        }
        return list
    }

    /**
     * Преобразовать в особь
     */
    //  companion object {
    //fun parseToIndividual(classes: List<StudentClassFull>): TimetableIndividual {

    //    val genomRooms = mutableListOf<Gene>()
    //    val genomTime = mutableListOf<Gene>()
    //    classes.forEach {
    //        genomRooms.add(it.classRoom)
    //        genomTime.add(it.time)
    //    }

    //    val chromosomeRooms = Chromosome(genomRooms)
    //    val chromosomeTime = Chromosome(genomTime)
    //    return TimetableIndividual(parseClasses(classes), mutableListOf(chromosomeRooms, chromosomeTime))
    //}

    //private fun parseClasses(classesFull: List<StudentClassFull>): List<StudentClass> {
    //    val classes = mutableListOf<StudentClass>()
    //    classesFull.forEach {
    //        classes.add(StudentClass(it.lesson, it.group, it.teacher))
    //    }
    //    return classes
    //}
    // }
}