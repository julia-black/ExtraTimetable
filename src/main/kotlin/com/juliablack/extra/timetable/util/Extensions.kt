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

fun List<Group>.findIdxGroup(number: String): Int {
    return this.indexOfFirst { it.number == number }
}

fun List<Group>.findGroup(number: String): Group? {
    return find { it.number.contains(number) }
}

fun Map<Int, Lesson>.findLesson(index: Int): Lesson? {
    return if (containsKey(index)) {
        get(index)
    } else {
        var resultIdx = 0
        forEach {
            if (it.key <= index) {
                resultIdx = it.key
            }
        }
        findLesson(resultIdx)
    }
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

fun Int.getTime(): String {
    return when (this) {
        0 -> "08:20\n09:50"
        1 -> "10:00\n11:35"
        2 -> "12:05\n13:40"
        3 -> "13:50\n15:25"
        4 -> "15:35\n17:10"
        5 -> "17:20\n18:40"
        6 -> "18:45\n20:05"
        else -> "20:10\n21:30"
    }
}