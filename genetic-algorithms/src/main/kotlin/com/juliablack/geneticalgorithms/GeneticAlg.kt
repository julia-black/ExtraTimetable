//package com.juliablack.geneticalgorithms
//
//import java.util.ArrayList
//import java.util.Comparator
//import java.util.LinkedHashMap
//import java.util.concurrent.ThreadLocalRandom
//
//import diophantine.model.Equation
//import diophantine.model.Term
//
//
//class GeneticSolver(
//    private val equation: Equation, private val generationSize: Int, private val mutationRate: Double,
//    private val survivalRate: Double
//) : Solver {
//
//    private val maxTermValue: Int
//
//    init {
//        this.maxTermValue = equation.getTerms().size() * 10
//    }
//
//    fun solve() {
//        val currentGeneration = Generation()
//
//        // Generate first generation randomly
//        for (i in 0 until generationSize) {
//            currentGeneration.addIndividual(createRandomIndividual())
//        }
//
//        // Search for solution
//        while (!currentGeneration.checkForSolution()) {
//            currentGeneration.generateNextGeneration()
//        }
//    }
//
//
//    private fun createRandomIndividual(): Individual {
//        val termValues = LinkedHashMap<Term, Int>()
//        for (term in equation.getTerms()) {
//            val value = ThreadLocalRandom.current().nextInt(0, maxTermValue + 1)
//            termValues[term] = value
//        }
//        val individual = Individual()
//        individual.termValues = termValues
//        return individual
//    }
//
//
//    private inner class Individual {
//
//        var termValues: Map<Term, Int> = LinkedHashMap<Term, Int>()
//
//
//        fun getFitness(targetValue: Int): Int {
//            var total = 0
//            for ((term, termValue) in termValues) {
//                total += term.getCoefficient() * termValue!!
//            }
//            return Math.abs(targetValue - total)
//        }
//
//    }
//
//    private inner class Generation {
//
//        private var count = 1
//
//        private val individuals = ArrayList<Individual>()
//
//        fun checkForSolution(): Boolean {
//            individuals.sortWith(Comparator { i1, i2 ->
//                val value = equation.getValue()
//                i1.getFitness(value) - i2.getFitness(value)
//            })
//            if (individuals[0].getFitness(equation.getValue()) == 0) {
//                println("Решение найдено на  $count поколении: ")
//                for ((term, termValue) in individuals[0].termValues) {
//                    System.out.println("\t" + term.getVariable() + ": " + termValue)
//                }
//                return true
//            }
//            return false
//        }
//
//        private fun selectFittest(): List<Individual> {
//            val fittest = ArrayList<Individual>()
//            var i = 0
//            while (i < survivalRate * generationSize) {
//                fittest.add(individuals[i])
//                i++
//            }
//            return fittest
//        }
//
//        private fun performCrossover(parent1: Individual, parent2: Individual): Individual {
//            val termValues = LinkedHashMap<Term, Int>()
//
//            for (term in equation.getTerms()) {
//                val parent1TermValue = parent1.termValues[term]
//                val parent2TermValue = parent2.termValues[term]
//                val average = Math.round(((parent1TermValue!! + parent2TermValue!!) / 2).toFloat())
//
//                // Randomize (Mutation)
//                val minBounds = Math.max(average - average * mutationRate, 0.0).toInt()
//                val maxBounds = average + minBounds
//
//                val newValue = ThreadLocalRandom.current().nextInt(minBounds, maxBounds + 1)
//                termValues[term] = newValue
//            }
//            val individual = Individual()
//            individual.termValues = termValues
//            return individual
//        }
//
//
//        fun generateNextGeneration() {
//            val fittest = selectFittest()
//            individuals.clear()
//            for (parent1 in fittest) {
//                for (parent2 in fittest) {
//                    val individual = performCrossover(parent1, parent2)
//                    individuals.add(individual)
//                }
//            }
//            // Include a random individual in every generation
//            individuals.add(createRandomIndividual())
//            count++
//        }
//
//        fun addIndividual(individual: Individual) {
//            individuals.add(individual)
//        }
//    }
//}
