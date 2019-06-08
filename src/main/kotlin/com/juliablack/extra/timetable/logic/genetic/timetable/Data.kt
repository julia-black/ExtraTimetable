package com.juliablack.extra.timetable.logic.genetic.timetable

import com.juliablack.extra.timetable.logic.genetic.common.Gene
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.DayOfWeek
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson

data class Group(var number: Int, var faculty: String, var countStudents: Int)

data class Teacher(
        var id : Int,
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
data class GroupProgramm(var group: Group,
                         var lessons: MutableMap<Lesson, Int>)

/**
 * Групповое расписание
 * @param timetable раписание по дням неделям, со списком соотвествий номер пары - пара
 */
data class GroupTimetable(var group: Group,
                          var timetable: Map<DayOfWeek, List<Triple<Int, ClassRoom, StudentClass>>>)


