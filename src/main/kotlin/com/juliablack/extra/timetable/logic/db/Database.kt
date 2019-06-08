package com.juliablack.extra.timetable.logic.db

import java.sql.DriverManager


object Database {

    val db = DriverManager.getConnection("jdbc:sqlite:timetable-database.db")
}
