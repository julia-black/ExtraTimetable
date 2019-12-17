package com.juliablack.extra.timetable.logic.db

import com.juliablack.extra.timetable.logic.genetic.GeneratorTimetable
import com.juliablack.extra.timetable.logic.genetic.timetable.ClassRoom
import com.juliablack.extra.timetable.logic.genetic.timetable.Group
import com.juliablack.extra.timetable.logic.genetic.timetable.GroupProgram
import com.juliablack.extra.timetable.logic.genetic.timetable.Lesson
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson
import io.reactivex.Observable
import org.nield.rxkotlinjdbc.select
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet


object Database {

    val db: Connection = DriverManager.getConnection("jdbc:sqlite:timetable-database.db")

    fun getGroups(): Observable<Group> {
        return db.select("SELECT * FROM ${DbContract.GROUP_TABLE}")
                .toObservable { Group(it.getInt(DbContract.NUMBER_GROUP), it.getString(DbContract.FACULTY), it.getInt(DbContract.COUNT)) }
    }

    fun getLessons(): Observable<Lesson> {
        return db.select("SELECT * FROM ${DbContract.LESSON_TABLE}")
                .toObservable {
                    Lesson(it.getString(DbContract.NAME_LESSON),
                            if (it.getString(DbContract.TYPE_LESSONS) == "Лекция")
                                TypeLesson.LECTURE
                            else TypeLesson.SEMINAR,
                            it.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                            it.getInt(DbContract.IS_NEED_PROJECTOR) == 1)
                }
    }

    fun getTeachers(): Observable<ResultSet> {
        return db.select("SELECT * FROM (${DbContract.TEACHER_TABLE} " +
                "JOIN ${DbContract.LESSON_TEACHER} USING (${DbContract.ID_TEACHER})) " +
                "JOIN  ${DbContract.LESSON_TABLE} USING (${DbContract.ID_LESSON})")
                .toObservable { res -> res}
    }

    fun getRooms() : Observable<ClassRoom> {
        return db.select("SELECT * FROM ${DbContract.ROOM_TABLE}")
                .toObservable {
                    ClassRoom(
                            it.getInt(DbContract.NUMBER),
                            it.getInt(DbContract.BUILDING),
                            it.getInt(DbContract.CAPACITY),
                            it.getInt(DbContract.COUNT_COMPUTERS) > 0,
                            it.getInt(DbContract.PROJECTOR) == 1
                    )
                }
    }

    fun getGroupsProgram() : Observable<ResultSet> {
        return db.select("SELECT * FROM (${DbContract.GROUP_TABLE} " +
                "JOIN ${DbContract.GROUPS_PROGRAM} USING (${DbContract.ID_GROUP})) " +
                "JOIN  ${DbContract.LESSON_TABLE} USING (${DbContract.ID_LESSON})")
                .toObservable { res -> res }

    }
}