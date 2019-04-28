package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.GeneticAlgorithm
import com.juliablack.geneticalgorithms.common.Individual

class TimetableGeneticAlg : GeneticAlgorithm() {

    private lateinit var population: MutableList<TimetableIndividual>

    override fun getPopulation(): MutableList<Individual> {
        return population as MutableList<Individual>
    }

    override fun setStartPopulation(population: List<Individual>) {
        this.population = population as MutableList<TimetableIndividual>
    }

    // override fun setStartPopulation(population: MutableList<Individual>) {
    //     TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    // }

    // override fun setStartPopulation(population: MutableList<TimetableIndividual>) {

    // }

    //Отбор лучших особей с помощью фитнесс-функции
    override fun generationPopulation() {

    }


}