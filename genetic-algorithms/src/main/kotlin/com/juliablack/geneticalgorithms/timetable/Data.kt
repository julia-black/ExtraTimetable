package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.Gene
import com.juliablack.geneticalgorithms.timetable.enums.DayOfWeek
import com.juliablack.geneticalgorithms.timetable.enums.TypeLesson

class Group(var number: Int, var countStudents: Int)

class Teacher(var name: String) //todo: добавить занятость

class Time(val dayOfWeek: DayOfWeek,
           val numberClass: Int) : Gene()

class ClassRoom(var numer: Int,
                var building: Int, //корпус
                var capacity: Int,
                var hasComputers: Boolean,
                var hasProjector: Boolean) : Gene()

class Lesson(var name: String,
             var teacher: Teacher,
             var typeLesson: TypeLesson,
             var isNeedComputers: Boolean)


class StudentClass(var lesson: Lesson,
                   var group: Group,
                   var time: Time,
                   var classRoom: ClassRoom)

//class StudentDay(val classes: List<StudentClass>)

//class GroupTimetable(private val week: MutableList<StudentDay?>)

