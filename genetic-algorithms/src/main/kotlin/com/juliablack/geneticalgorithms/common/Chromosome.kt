package com.juliablack.geneticalgorithms.common

/**
 * Хромосома - набор генов
 */
class Chromosome(private val genom: MutableList<Gene>) {

    fun getGen(locus: Int): Gene = genom[locus]

    fun setGen(gene: Gene, locus: Int) {
        genom.add(locus, gene)
    }

    fun getIndexes(gene: Gene): List<Int> {
        val list = mutableListOf<Int>()
        genom.forEachIndexed { index, it ->
            if (it == gene)
                list.add(index)
        }
        return list
    }

    fun getGenom() = genom

    //fun indexOfGen(gene: Gene): Int = genom.indexOf(gene)
}