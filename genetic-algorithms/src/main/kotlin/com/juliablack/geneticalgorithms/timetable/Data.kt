package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.Gene
import com.juliablack.geneticalgorithms.timetable.enums.DayOfWeek
import com.juliablack.geneticalgorithms.timetable.enums.TypeLesson

data class Group(var number: Int, var countStudents: Int)

data class Teacher(var name: String,
                   var lessons: List<Lesson>) //todo: добавить занятость

data class Time(val dayOfWeek: DayOfWeek,
                val numberClass: Int) : Gene()

data class ClassRoom(var numer: Int,
                     var building: Int, //корпус
                     var capacity: Int,
                     var hasComputers: Boolean,
                     var hasProjector: Boolean) : Gene()

data class Lesson(var name: String,
                  var typeLesson: TypeLesson,
                  var isNeedComputers: Boolean)

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
                         var lessons: Map<Lesson, Int>)

/**
 * Групповое расписание
 * @param timetable раписание по дням неделям, со списком соотвествий номер пары - пара
 */
data class GroupTimetable(var group: Group,
                          var timetable: Map<DayOfWeek, List<Triple<Int, ClassRoom, StudentClass>>>)


