package com.juliablack.extra.timetable.logic.genetic.timetable.enums

import java.util.Random

enum class DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY; //THURSDAY, FRIDAY, SATURDAY;

    companion object {
        fun getRandomDay(): DayOfWeek {
            return DayOfWeek.values()[Random().nextInt(values().size)]
        }
    }
}