package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.Chromosome
import com.juliablack.geneticalgorithms.common.Gene

/**
 * Расписание
 */
class Timetable(private val classes: List<StudentClassFull>) {

    fun getClasses() = classes

    /**
     * Преобразовать в особь
     */
    companion object {
       //fun parseToIndividual(classes: List<StudentClassFull>): TimetableIndividual {

       //    val genomRooms = mutableListOf<Gene>()
       //    val genomTime = mutableListOf<Gene>()
       //    classes.forEach {
       //        genomRooms.add(it.classRoom)
       //        genomTime.add(it.time)
       //    }

       //    val chromosomeRooms = Chromosome(genomRooms)
       //    val chromosomeTime = Chromosome(genomTime)
       //    return TimetableIndividual(parseClasses(classes), mutableListOf(chromosomeRooms, chromosomeTime))
       //}

       //private fun parseClasses(classesFull: List<StudentClassFull>): List<StudentClass> {
       //    val classes = mutableListOf<StudentClass>()
       //    classesFull.forEach {
       //        classes.add(StudentClass(it.lesson, it.group, it.teacher))
       //    }
       //    return classes
       //}
    }
}