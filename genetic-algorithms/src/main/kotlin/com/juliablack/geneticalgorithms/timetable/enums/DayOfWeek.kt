package com.juliablack.geneticalgorithms.timetable.enums

import java.util.Random

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    companion object {
        fun getRandomDay(): DayOfWeek {
            return DayOfWeek.values()[Random().nextInt(values().size)]
        }
    }
}