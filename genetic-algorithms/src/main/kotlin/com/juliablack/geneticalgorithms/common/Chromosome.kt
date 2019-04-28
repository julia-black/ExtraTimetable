package com.juliablack.geneticalgorithms.common

/**
 * Хромосома - набор генов
 */
class Chromosome(private val genom: MutableList<Gene>) {

    fun getGen(locus: Int): Gene = genom[locus]

    fun setGen(gene: Gene, locus: Int) {
        genom[locus] = gene
    }
}