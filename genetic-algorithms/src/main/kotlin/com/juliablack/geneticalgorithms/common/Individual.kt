package com.juliablack.geneticalgorithms.common


interface Individual {

    /**
     * Вычислить фитнесс-функцию (приспособления)
     */
    fun calculateFitnessFunction()

    /**
     * Произвести мутацию, например, поменять какие-то элементы во всех хромососах местами
     */
    fun mutation()

    fun getChromosomes() : List<Chromosome>
}

///**
// * Особь, в данном слчае - расписание, содержит хромосому (м.б. более одной, но сделаем сначала с одной)
// */
//interface Individual {
//
//    /**
//     * Вычислить фитнесс-функцию (приспособления)
//     */
//    //fun calculateFitnessFunction()
//
//
//}