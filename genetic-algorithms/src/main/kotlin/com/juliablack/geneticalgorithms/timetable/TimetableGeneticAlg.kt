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

    //Отбор лучших особей с помощью фитнесс-функции
    override fun generationPopulation() {

        population.forEach {
            it.calculateFitnessFunction()
        }
        System.out.println(population.toString())
    }

}