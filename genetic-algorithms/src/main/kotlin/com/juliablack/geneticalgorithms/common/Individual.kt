package com.juliablack.geneticalgorithms.common


interface Individual {

    /**
     * Вычислить фитнесс-функцию (приспособления)
     */
    fun calculateFitnessFunction() : Int

    /**
     * Произвести мутацию, например, поменять какие-то элементы во всех хромососах местами
     */
    fun mutation()

    fun getChromosomes() : List<Chromosome>
}