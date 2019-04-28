package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.Chromosome
import com.juliablack.geneticalgorithms.common.Individual

class TimetableIndividual(private val chromosomes: MutableList<Chromosome>) : Individual {

    override fun calculateFitnessFunction() {

    }

    override fun mutation() {

    }

    override fun getChromosomes() = chromosomes


}