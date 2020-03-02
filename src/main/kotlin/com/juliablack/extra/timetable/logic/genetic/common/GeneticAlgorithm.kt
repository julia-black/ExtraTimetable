package com.juliablack.extra.timetable.logic.genetic.common

import com.juliablack.extra.timetable.logic.genetic.timetable.Const
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.CrossoverType
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.MutationType
import kotlin.random.Random

abstract class GeneticAlgorithm {

    /**
     * Генерация промежуточной популяции путем отбора
     */
    abstract fun generationPopulation()

    abstract fun getPopulation(): MutableList<Individual>

    abstract fun setStartPopulation(population: List<Individual>)

    /**
     * Скрещивание. Берем 2 случайных особи (из уже отобранных) и добавляем в популяцию их "детей"
     */
    fun crossover(type: CrossoverType) {
        var countCrossover = 0
        for (i in 0 until getPopulation().size / 2) {

            var idxIndividual1 = 0
            var idxIndividual2 = 0
            when (type) {
                //Положительно ассоциативное скрещивание -
                //выбираются пары с близким значениями оценки приспособленности
                CrossoverType.POSITIVE_ASSOCIATIVE -> {
                    getPopulation().sortBy { it.calculateFitnessFunction() }
                    idxIndividual1 = i
                    idxIndividual2 = i + 1
                }
                //Негативно ассоциативное скрещивание -
                //выбираются пары с дальними значениями оценки приспособленности
                CrossoverType.NEGATIVE_ASSOCIATIVE -> {
                    getPopulation().sortBy { it.calculateFitnessFunction() }
                    idxIndividual1 = i
                    idxIndividual2 = getPopulation().size / 2 - 1
                }
                else -> { //Панмиксия - пара выбирается случайным образом
                    idxIndividual1 = Random.nextInt(getPopulation().size - 1)
                    idxIndividual2 = Random.nextInt(getPopulation().size - 1)
                    while (idxIndividual1 == idxIndividual2) {
                        idxIndividual2 = Random.nextInt(getPopulation().size - 1)
                    }
                }
            }

            val child1 = getPopulation()[idxIndividual1]
            val child2 = getPopulation()[idxIndividual2]

            val possibleLocuses = getPossibleCrossovers(idxIndividual1, idxIndividual2)

            if (!possibleLocuses.isEmpty()) {
                countCrossover++
                val locus = Random.nextInt(0, possibleLocuses.size - 1)

                //У каждой особи меняются данные гены местами для каждой хромосомы
                child1.getChromosomes().forEachIndexed { index, chromosome ->
                    val gene = child2.getChromosomes()[index].getGen(locus)
                    child2.getChromosomes()[index].setGen(chromosome.getGen(locus), locus)
                    chromosome.setGen(gene, locus)
                }
            }
            getPopulation().apply {
                add(child1)
                add(child2)
            }
        }
    }

    fun mutation(type: MutationType) {
        when (type) {
            MutationType.POINT_MUTATION -> {
                val idx = Random.nextInt(0, getPopulation().size - 1)
                getPopulation()[idx].mutation()
            }
            MutationType.LARGE_MUTATION -> {
                mutationAll(Const.PROBABILITY_MUTATION)
            }
        }
    }

    /**
     * Мутация всех особей
     * @param probability вероятность мутации определенного гена
     */
    fun mutationAll(probability: Double) {
        getPopulation().forEach {
            if (Random.nextDouble(0.0, 1.0) > probability)
                it.mutation()
        }
    }

    protected fun removeIndividual(element: Individual) {
        getPopulation().remove(element)
    }

    protected fun addIndividual(element: Individual) {
        getPopulation().add(element)
    }

    abstract fun getBestIndividual(count: Int = 1): Individual

    abstract fun getPossibleCrossovers(idxIndividual1: Int, idxIndividual2: Int): List<Int>

    abstract fun getMeanFitnessFunction() : Float
}