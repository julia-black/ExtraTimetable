package com.juliablack.extra.timetable.logic.genetic.timetable

import com.juliablack.extra.timetable.logic.genetic.common.GeneticAlgorithm
import com.juliablack.extra.timetable.logic.genetic.common.Individual


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
        population.sortBy { it.fitnessFunction }

        System.out.println(population.toString())

        val count = population.size
        population.removeIf {
            population.indexOf(it) < count / 2
        }
        System.out.println(population.toString())
    }

}