package com.juliablack.extra.timetable.util

fun String.containsIgnoreCase(string: String): Boolean =
        this.toLowerCase().contains(string.toLowerCase())