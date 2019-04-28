package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.Chromosome
import com.juliablack.geneticalgorithms.common.Gene

/**
 * Расписание -
 */
class Timetable(private val classes: List<StudentClass>) {

    fun getClasses() = classes

    /**
     * Преобразовать в особь
     */
    companion object {
        fun parseToIndividual(classes: List<StudentClass>): TimetableIndividual {

            val genomRooms = mutableListOf<Gene>()
            val genomTime = mutableListOf<Gene>()
            classes.forEach {
                genomRooms.add(it.classRoom)
                genomTime.add(it.time)
            }

            val chromosomeRooms = Chromosome(genomRooms)
            val chromosomeTime = Chromosome(genomTime)
            return TimetableIndividual(mutableListOf(chromosomeRooms, chromosomeTime))
        }
    }
}