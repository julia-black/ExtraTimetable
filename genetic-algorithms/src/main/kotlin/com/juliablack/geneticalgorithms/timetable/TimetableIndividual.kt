package com.juliablack.geneticalgorithms.timetable

import com.juliablack.geneticalgorithms.common.Chromosome
import com.juliablack.geneticalgorithms.common.Individual
import com.juliablack.geneticalgorithms.timetable.enums.DayOfWeek

/**
 * Особь (расписание). Classes служат индексами в хромосомах
 */
class TimetableIndividual : Individual {
    var optionalLessonsOfDay: Int? = null
    var groups: List<Group>? = null

    private var classes: MutableList<StudentClass>
    private var chromosomes: MutableList<Chromosome>

    private var fitnessFunction: Int? = null

    constructor() {
        classes = mutableListOf()
        chromosomes = mutableListOf()
        chromosomes.add(Chromosome(mutableListOf()))
        chromosomes.add(Chromosome(mutableListOf()))
    }

    constructor(classes: MutableList<StudentClass>, chromosomes: MutableList<Chromosome>) {
        this.classes = classes
        this.chromosomes = chromosomes
    }

    override fun calculateFitnessFunction(): Int {
        //todo: здесь будут условия, которые должны быть выполнены. за каждое выполненное условие +1
        var result = 0
        groups ?: throw Exception("Не переданы учебные группы")

        val timetable = Timetable(this)

        groups!!.forEach { group ->
           // val listClasses = getFullClasses(group)
            //Проверка оптимального количества пар в день
            optionalLessonsOfDay?.let { optionalLessonsOfDay ->
                DayOfWeek.values().forEach {
                    val diff = getCountLessonsOfDay(group, it) - optionalLessonsOfDay
                    if (diff > 0) {
                        result -= diff
                    }
                }
            }
            //Проверка окон


        }
        //Проверка аудиторий
        getRooms().getGenom().forEachIndexed { index, gene ->
            (gene as ClassRoom).apply {
                //Вместимость
                val studentClass = getClasses()[index]
                if (studentClass.group.countStudents > capacity)
                    result--
            }
        }

        //Соответсвие аудитории размеру
        fitnessFunction = result
        return result
    }

    /**
     * Получить количество окон в расписании
     * @return Pair<Количество ненужных окон, количество нужных окон>
     */
    private fun getIntervals(list: List<StudentClassFull>): Pair<Int,Int> {
        val countBadIntervals = 0
        val countGoodIntervals = 0

        list.forEach {
            DayOfWeek.values().forEach {

            }
        }
        return Pair(countBadIntervals, countGoodIntervals)
    }

    private fun getCountLessonsOfDay(group: Group, dayOfWeek: DayOfWeek): Int {
        var count = 0
        //если в списке аудиторий уже есть такая
        getTimes().getGenom().forEachIndexed { index, gene ->
            if (getClasses()[index].group == group) {
                (gene as Time).apply {
                    if (this.dayOfWeek == dayOfWeek)
                        count++
                }
            }
        }
        return count
    }

    /**
     * Получение кол-ва окон у группы
     */
    private fun getFullClasses(group: Group): List<StudentClassFull> {
        val list = mutableListOf<StudentClassFull>()
        getClasses().forEachIndexed { index, studentClass ->
            val studentClassFull = StudentClassFull(studentClass.lesson,
                    studentClass.group,
                    studentClass.teacher,
                    getTimes().getGen(index) as Time,
                    getRooms().getGen(index) as ClassRoom)
            list.add(studentClassFull)
        }
        return list
    }

    override fun mutation() {

    }

    override fun getChromosomes() = chromosomes

    fun getRooms() = chromosomes[0]

    fun getTimes() = chromosomes[1]

    fun getClasses() = classes

    fun addItem(studentClass: StudentClass, time: Time, room: ClassRoom) {
        val idx = classes.size
        classes.add(studentClass)
        getRooms().setGen(room, idx)
        getTimes().setGen(time, idx)
    }
}