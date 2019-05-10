package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.Chromosome
import com.juliablack.geneticalgorithms.common.Individual

/**
 * Особь (расписание). Classes служат индексами в хромосомах
 */
class TimetableIndividual : Individual {
    private var classes: MutableList<StudentClass>
    private var chromosomes: MutableList<Chromosome>

    constructor() {
        classes = mutableListOf()
        chromosomes = mutableListOf()
        chromosomes.add(Chromosome(mutableListOf()))
        chromosomes.add(Chromosome(mutableListOf()))
    }

    constructor(classes: MutableList<StudentClass>, chromosomes: MutableList<Chromosome>) {
        this.classes = classes
        this.chromosomes = chromosomes
    }

    override fun calculateFitnessFunction() {

    }

    override fun mutation() {

    }

    override fun getChromosomes() = chromosomes

    fun getRooms() = chromosomes[0]

    fun getTimes() = chromosomes[1]

    fun getClasses() = classes

    fun addItem(studentClass: StudentClass, time: Time, room: ClassRoom) {
        val idx = classes.size
        classes.add(studentClass)
        getRooms().setGen(room, idx)
        getTimes().setGen(time, idx)
    }
}