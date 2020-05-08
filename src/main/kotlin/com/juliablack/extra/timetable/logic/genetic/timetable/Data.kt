package com.juliablack.extra.timetable.logic.genetic.timetable

import com.juliablack.extra.timetable.logic.genetic.common.Gene
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.DayOfWeek
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson

data class Group(var number: String, var countStudents: Int, var subGroups: MutableList<Group> = mutableListOf()) //подугрппа - номер 1/2/3

data class Teacher(
        var id: Int,
        var name: String,
        var lessons: MutableList<Lesson> = mutableListOf()) //todo: добавить занятость

data class Time(val dayOfWeek: DayOfWeek,
                val numberClass: Int) : Gene()

data class ClassRoom(var number: Int,
                     var building: Int, //корпус
                     var capacity: Int,
                     var hasComputers: Boolean,
                     var hasProjector: Boolean) : Gene()

data class Lesson(var name: String,
                  var typeLesson: TypeLesson,
                  var isNeedComputers: Boolean,
                  var isNeedProjector: Boolean)

data class StudentClass(var lesson: Lesson,
                        var group: Group,
                        var teacher: Teacher)

data class StudentClassFull(var lesson: Lesson,
                            var group: Group,
                            var teacher: Teacher,
                            var time: Time,
                            var classRoom: ClassRoom)

/**
 * Учебная программа группы
 * @param lessons - мапа лекций с указанием количества пар в неделю
 */
data class GroupProgram(var numGroup: String,
                        var lessons: MutableMap<Lesson, Int>)

data class DayClass(var dayOfWeek: DayOfWeek,
                    var classes: MutableList<SimpleClass>)

data class SimpleClass(var time: Int, //номер пары
                       var teacherName: String, //ФИО преподователя
                       var lessonName: String,
                       var type: String, //Лекия или практика
                       var room: Int,
                       var building: Int) {

    override fun toString(): String {
        val typeStr = when (type) {
            TypeLesson.LECTURE.toString() -> "лек."
            TypeLesson.LABORATORY.toString() -> "пр."
            else -> "сем."
        }
        return "$typeStr\n$lessonName\n$teacherName\n$room, корпус $building"
    }
}

data class GroupTimetable(var group: Group,
                          var list: List<DayClass>)

class GroupTimetableForView {
    var time = ""
    var monday = ""
    var tuesday = ""
    var wednesday = ""
    var thursday = ""
    var friday = ""
    var saturday = ""
}