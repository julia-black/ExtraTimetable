package com.juliablack.extra.timetable.logic.db

import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import org.nield.rxkotlinjdbc.execute
import org.nield.rxkotlinjdbc.insert
import java.awt.dnd.DnDConstants
import java.sql.DriverManager

val db = DriverManager.getConnection("jdbc:sqlite:timetable-database.db").apply {

//    //Таблица Teachers
//    execute("CREATE TABLE IF NOT EXISTS ${DbContract.TEACHER_TABLE} " +
//            "(${DbContract.ID} INTEGER PRIMARY KEY, ${DbContract.NAME} VARCHAR)")
//            .toSingle()
//            .subscribe()
//
//    listOf(
//            "Иванов И.И.",
//            "Петров П.П.",
//            "Сидоров С.С.",
//            "Никитин Н.Н.",
//            "Константинова К.К.",
//            "Мишина М.М.",
//            "Сергеева С.С."
//    ).toObservable()
//            .flatMap {
//                insert("INSERT OR REPLACE INTO ${DbContract.TEACHER_TABLE} (" +
//                        "${DbContract.NAME}) " +
//                        "VALUES (:${DbContract.NAME})" +
//                        ")")
//                        .parameter(DbContract.NAME, it)
//                        .toObservable { item -> item.getInt(1) }
//            }
//            .toList()
//            .subscribeBy(
//                    onSuccess = { println("TEACHER table created, KEYS: $it") },
//                    onError = { throw RuntimeException(it) }
//            )
//
//    //create SALES_PERSON TABLE
//    execute("CREATE TABLE IF NOT EXISTS ${DbContract.GROUP_TABLE} (" +
//            "${DbContract.ID} INTEGER PRIMARY KEY, " +
//            "${DbContract.NUMBER_GROUP} INTEGER, " +
//            "${DbContract.FACULTY} VARCHAR, " +
//            "${DbContract.COUNT} INTEGER" +
//            ")").toSingle().subscribe()
//
//    listOf(
//            111 to "КНИИТ" to 25,
//            121 to "КНИИТ" to 30,
//            131 to "КНИИТ" to 35,
//            211 to "КНИИТ" to 27,
//            221 to "КНИИТ" to 25,
//            231 to "КНИИТ" to 25
//    ).toObservable()
//            .flatMapSingle {
//                insert("INSERT OR REPLACE ${DbContract.GROUP_TABLE} (" +
//                        "${DbContract.NUMBER_GROUP},${DbContract.FACULTY},${DbContract.COUNT}) " +
//                        "VALUES (:${DbContract.NUMBER_GROUP},:${DbContract.FACULTY},:${DbContract.COUNT})")
//                        .parameter(DbContract.NUMBER_GROUP, it.first.first)
//                        .parameter(DbContract.FACULTY, it.first.second)
//                        .parameter(DbContract.COUNT, it.second)
//                        .toSingle { it.getInt(1) }
//            }.subscribeBy(
//                    onNext = { println("STUDENT_GROUP table created, KEYS: $it") },
//                    onError = { throw RuntimeException(it) }
//            )
}
