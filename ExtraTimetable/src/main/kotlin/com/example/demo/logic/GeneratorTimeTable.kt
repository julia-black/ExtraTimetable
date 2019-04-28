package com.example.demo.logic

import com.juliablack.geneticalgorithms.common.GeneticAlgorithm
import com.juliablack.geneticalgorithms.timetable.*
import com.juliablack.geneticalgorithms.timetable.enums.DayOfWeek
import com.juliablack.geneticalgorithms.timetable.enums.TypeLesson

class GeneratorTimeTable {

    private lateinit var rooms: List<ClassRoom>
    private lateinit var lessons: List<Lesson>
    private lateinit var groups: List<Group>

    init {
        downloadTimetableFromDB()
        val population: List<TimetableIndividual> = generateStartPopulation()
        val geneticAlg: GeneticAlgorithm = TimetableGeneticAlg()
        geneticAlg.setStartPopulation(population)
    }

    private fun generateStartPopulation(): List<TimetableIndividual> { 
        //todo:здесь будет составляться первые N раписаний, который будут составляться рандомно (но с учетом мин. требований)
        val population: MutableList<TimetableIndividual> = mutableListOf()
        val listClasses: MutableList<StudentClass> = mutableListOf()
        lessons.forEach {
            listClasses.add(StudentClass(it, groups[0], Time(DayOfWeek.MONDAY, 1), rooms[0]))
        }
        population.add(Timetable.parseToIndividual(listClasses))
        lessons.forEach {
            listClasses.add(StudentClass(it, groups[1], Time(DayOfWeek.MONDAY, 1), rooms[0]))
        }
        population.add(Timetable.parseToIndividual(listClasses))
        return population
    }

    private fun downloadTimetableFromDB() {
        //todo:здесь будет загрузка из базы учителей, пар, групп, классов и т.д.
        val teacher1 = Teacher("Иванов И.И.")
        val teacher2 = Teacher("Степанов С.С.")
        val teacher3 = Teacher("Попова М.В.")

        val lesson = Lesson("Программирование", teacher1, TypeLesson.LECTURE, false)
        val lesson1 = Lesson("Программирование", teacher1, TypeLesson.PRACTICE, true)
        val lesson2 = Lesson("Анализ данных", teacher2, TypeLesson.LECTURE, false)
        val lesson3 = Lesson("Математический анализ", teacher3, TypeLesson.LECTURE, false)

        lessons = listOf(lesson, lesson1, lesson2, lesson3)

        val classRoom100 = ClassRoom(100, 12, 30, false, true)
        val classRoom101 = ClassRoom(101, 12, 50, false, true)
        val classRoom222 = ClassRoom(222, 12, 25, true, false)

        rooms = listOf(classRoom100, classRoom101, classRoom222)
        groups = listOf(Group(121, 20), Group(122, 23))
    }
}