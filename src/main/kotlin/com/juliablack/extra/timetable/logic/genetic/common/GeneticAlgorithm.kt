package com.juliablack.extra.timetable.logic.genetic.common

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
    fun crossover() {
        for (i in 0 until getPopulation().size / 2) {
            //Берется 2 случайных особи
            val idxIndividual1 = Random.nextInt(getPopulation().size - 1)
            var idxIndividual2 = Random.nextInt(getPopulation().size - 1)

            while (idxIndividual1 == idxIndividual2) {
                idxIndividual2 = Random.nextInt(getPopulation().size - 1)
            }

            //Берется случайная позиция гена в хромосоме (локус)
            val locus = Random.nextInt(0, getPopulation()[idxIndividual1].getChromosomes()[0].getGenom().size - 1)

            val child1 = getPopulation()[idxIndividual1]
            val child2 = getPopulation()[idxIndividual2]

            //У каждой особи меняются данные гены местами для каждой хромосомы
            child1.getChromosomes().forEachIndexed { index, chromosome ->

                val gene = child2.getChromosomes()[index].getGen(locus)
                child2.getChromosomes()[index].setGen(chromosome.getGen(locus), locus)
                chromosome.setGen(gene, locus)
            }

           getPopulation().apply {
               add(child1)
               add(child2)
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
}