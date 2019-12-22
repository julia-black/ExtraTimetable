package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.Lesson

fun String.containsIgnoreCase(string: String): Boolean =
        this.toLowerCase().contains(string.toLowerCase())

fun List<Lesson>.containsLesson(lesson: Lesson): Boolean {
    this.find {
        it.name == lesson.name && it.typeLesson == lesson.typeLesson && it.isNeedComputers == lesson.isNeedComputers
                && it.isNeedProjector == lesson.isNeedProjector
    }?.let {
        return true
    }
    return false
}