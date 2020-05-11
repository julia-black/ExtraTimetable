package com.juliablack.extra.timetable.logic.genetic.timetable

import com.juliablack.extra.timetable.logic.genetic.common.GeneticAlgorithm
import com.juliablack.extra.timetable.logic.genetic.common.Individual
import com.juliablack.extra.timetable.util.Util


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

        Util.printFitnessFunctions(population)

        val count = population.size
        for (i in 0 until count / 2) {
            population.removeAt(0)
        }
    }


    override fun getMeanFitnessFunction() : Float {
        val sum = population.sumBy { it.fitnessFunction!! }
        return sum.toFloat() / population.size.toFloat()
    }

    override fun getBestIndividual(count: Int): Individual {
        population.sortBy { it.fitnessFunction }
        return population.last()
    }

    override fun getPossibleCrossovers(idxIndividual1: Int, idxIndividual2: Int): List<Int> {
        val possibleLocuses = mutableListOf<Int>()
        val individual1 = population[idxIndividual1]
        val individual2 = population[idxIndividual2]
        val groups = individual1.groups!!

        individual1.getTimes().getGenom().forEachIndexed { index, timeGene ->
            if (isPossibleCrossover(
                            groups,
                            individual1,
                            individual2,
                            individual1.getClasses()[index].teacher,
                            individual1.getClasses()[index].group,
                            index)) {
                possibleLocuses.add(index)
            }
        }
        return possibleLocuses
    }

    private fun isPossibleCrossover(groups: List<Group>, individual1: TimetableIndividual, individual2: TimetableIndividual,
                                    teacher: Teacher, group: Group, locus: Int): Boolean {
        val result = Util.isTimeFree(
                individual1,
                individual2.getTimes().getGen(locus) as Time,
                individual2.getRooms().getGen(locus) as ClassRoom,
                group) &&
                Util.isTimeOnTeacherFree(
                        individual1,
                        individual2.getTimes().getGen(locus) as Time,
                        teacher,
                        groups) &&
                Util.isTimeFree(
                        individual2,
                        individual1.getTimes().getGen(locus) as Time,
                        individual1.getRooms().getGen(locus) as ClassRoom,
                        group) &&
                Util.isTimeOnTeacherFree(
                        individual2,
                        individual1.getTimes().getGen(locus) as Time,
                        teacher,
                        groups
                )
        return result
    }
}