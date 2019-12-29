package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.Group
import com.juliablack.extra.timetable.logic.genetic.timetable.Lesson

fun String.containsIgnoreCase(string: String): Boolean =
        this.toLowerCase().contains(string.toLowerCase())

fun Map<Int, Lesson>.containsLesson(lesson: Lesson): Boolean {
    forEach {
        if (it.value.name == lesson.name && it.value.typeLesson == lesson.typeLesson && it.value.isNeedComputers == lesson.isNeedComputers
                && it.value.isNeedProjector == lesson.isNeedProjector) {
            return true
        }
    }
    return false
}

fun List<Group>.findGroup(number: String): Int {
    return this.indexOfFirst { it.number == number }
}

fun Map<Int, Lesson>.findLesson(index: Int): Lesson? {
    if (containsKey(index)) {
        return get(index)
    } else {
        forEach {
            if (it.key >= index) {
                return it.value
            }
        }
    }
    return null
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