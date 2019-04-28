package com.juliablack.geneticalgorithms.common

import kotlin.random.Random

abstract class GeneticAlgorithm {

    /**
     * Генерация промежуточной популяции путем отбора
     */
    abstract fun generationPopulation()

    abstract fun getPopulation(): MutableList<Individual>

    abstract fun setStartPopulation(population: List<Individual>)

    /**
     * Скрещивание
     */
    fun crossover() {
        //Берется 2 случайных особи
        val idxIndividual1 = Random.nextInt(0, getPopulation().size - 1)
        var idxIndividual2 = Random.nextInt(0, getPopulation().size - 1)

        while (idxIndividual1 == idxIndividual2) {
            idxIndividual2 = Random.nextInt(0, getPopulation().size - 1)
        }

        //Берется случайная позиция гена в хромосоме (локус)
        val locus = Random.nextInt(0, getPopulation()[idxIndividual1].getChromosomes().size - 1)

        val individual1 = getPopulation()[idxIndividual1]
        val individual2 = getPopulation()[idxIndividual2]

        //У каждой особи меняются данные гены местами для каждой хромосомы
        individual1.getChromosomes().forEachIndexed { index, chromosome ->
            val gene = individual2.getChromosomes()[index].getGen(locus)
            individual2.getChromosomes()[index].setGen(chromosome.getGen(locus), locus)
            chromosome.setGen(gene, locus)
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

    /**
     * Устанавливает начальную популяцию
     */
   //fun setStartgetPopulation()(getPopulation(): MutableList<Individual>) {
   //    this.getPopulation() = getPopulation()
   //}

   // protected fun getgetPopulation()() = getPopulation()

    protected fun removeIndividual(element: Individual) {
        getPopulation().remove(element)
    }

    protected fun addIndividual(element: Individual) {
        getPopulation().add(element)
    }
}