package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.Gene
import com.juliablack.geneticalgorithms.timetable.enums.DayOfWeek
import com.juliablack.geneticalgorithms.timetable.enums.TypeLesson

class Group(var number: Int, var countStudents: Int)

class Teacher(var name: String,
              var lessons: List<Lesson>) //todo: добавить занятость

class Time(val dayOfWeek: DayOfWeek,
           val numberClass: Int) : Gene()

class ClassRoom(var numer: Int,
                var building: Int, //корпус
                var capacity: Int,
                var hasComputers: Boolean,
                var hasProjector: Boolean) : Gene()

class Lesson(var name: String,
             var typeLesson: TypeLesson,
             var isNeedComputers: Boolean)

class StudentClass(var lesson: Lesson,
                   var group: Group,
                   var teacher: Teacher)

class StudentClassFull(var lesson: Lesson,
                       var group: Group,
                       var teacher: Teacher,
                       var time: Time,
                       var classRoom: ClassRoom)

/**
 * Учебная программа группы
 * @param lessons - мапа лекций с указанием количества пар в неделю
 */
class GroupProgramm(var group: Group,
                    var lessons: Map<Lesson, Int>)


