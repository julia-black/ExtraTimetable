package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.Group
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

fun List<Group>.findGroup(number: String): Int {
    return this.indexOfFirst { it.number == number }
}

fun MutableList<MutableList<String>>.getColumn(idxColumn: Int): List<String> {
    val result = mutableListOf<String>()
    forEach { row ->
        if (row.size > idxColumn) {
            result.add(row[idxColumn])
        }
    }
    return result
}