package com.juliablack.extra.timetable.logic.genetic.timetable

import com.juliablack.extra.timetable.logic.genetic.GeneratorTimetable
import com.juliablack.extra.timetable.logic.genetic.common.Chromosome
import com.juliablack.extra.timetable.logic.genetic.common.Individual
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.DayOfWeek
import com.juliablack.extra.timetable.util.Util
import java.util.*

/**
 * Особь (расписание). Classes служат индексами в хромосомах
 */
class TimetableIndividual : Individual {
    var optionalLessonsOfDay: Int? = null
    var groups: List<Group>? = null

    private var classes: MutableList<StudentClass>
    private var chromosomes: MutableList<Chromosome>
    private var maxLessonOfDay: Int

    var fitnessFunction: Int? = null

    constructor(maxLessonOfDay: Int) {
        classes = mutableListOf()
        chromosomes = mutableListOf()
        chromosomes.add(Chromosome(mutableListOf()))
        chromosomes.add(Chromosome(mutableListOf()))
        this.maxLessonOfDay = maxLessonOfDay
    }

    constructor(classes: MutableList<StudentClass>, chromosomes: MutableList<Chromosome>, maxLessonOfDay: Int) {
        this.classes = classes
        this.chromosomes = chromosomes
        this.maxLessonOfDay = maxLessonOfDay
    }

    override fun calculateFitnessFunction(): Int {
        var result = 0
        groups ?: throw Exception("Не переданы учебные группы")

        groups!!.forEach { group ->
            //Проверка оптимального количества пар в день
            optionalLessonsOfDay?.let { optionalLessonsOfDay ->
                DayOfWeek.values().forEach {
                    val diff = getCountLessonsOfDay(group, it) - optionalLessonsOfDay
                    if (diff > 0) {
                        result -= diff
                    }
                    if (getCountLessonsOfDay(group, it) > maxLessonOfDay) {
                        result--
                    }
                }
            }
        }
        //Проверка аудиторий
        getRooms().getGenom().forEachIndexed { index, gene ->
            (gene as ClassRoom).apply {
                val studentClass = getClasses()[index]
                when {
                    studentClass.group.countStudents > capacity -> result-- //Вместимость
                    studentClass.lesson.isNeedComputers && !hasComputers -> result-- //Наличие компьютеров
                    studentClass.lesson.isNeedProjector && !hasProjector -> result-- //Наличие проектора
                }
            }
        }

        val timetable = Timetable(this)
        //количество окон
        result -= timetable.getCountInterval()

        //Соответсвие аудитории размеру
        fitnessFunction = result
        return result
    }

    private fun getCountLessonsOfDay(group: Group, dayOfWeek: DayOfWeek): Int {
        var count = 0
        //если в списке аудиторий уже есть такая
        getTimes().getGenom().forEachIndexed { index, gene ->
            try {
                if (getClasses()[index].group == group) {
                    (gene as Time).apply {
                        if (this.dayOfWeek == dayOfWeek)
                            count++
                    }
                }
            } catch (e: Exception) {
                System.out.println("")
            }
        }
        return count
    }

    fun getFullClasses(group: Group): List<StudentClassFull> {
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

    /**
     * Мутация - рандомный ген выбирается заново.
     */
    override fun mutation() {
        //Выыбираем рандомный локус
        val locus = Random().nextInt(getTimes().getGenom().size)
        //Генерируем новую пару
        val studentClass = getClasses()[locus]
        val oldTime = getTimes().getGen(locus)
        val oldRoom = getRooms().getGen(locus)
        val oldFitness = calculateFitnessFunction()
        val triple = GeneratorTimetable
                .generationTriple(
                        studentClass.lesson,
                        studentClass.group,
                        this,
                        maxLessonOfDay)
        getTimes().setGen(triple.second, locus)
        getRooms().setGen(triple.third, locus)
        if (calculateFitnessFunction() < oldFitness) {
            getTimes().setGen(oldTime, locus)
            getRooms().setGen(oldRoom, locus)
        }
    }

    override fun getChromosomes() = chromosomes

    fun getRooms() = chromosomes[0]

    fun getTimes() = chromosomes[1]

    fun getClasses() = classes

    fun addItem(studentClass: StudentClass, time: Time, room: ClassRoom) {
        val idx = classes.size
        classes.add(studentClass)
        getRooms().addGen(idx, room)
        getTimes().addGen(idx, time)
    }

    fun getRandomFreeTime(room: ClassRoom, teacher: Teacher, group: Group, groups: List<Group>): Time {
        val freeTimes = mutableListOf<Time>()
        DayOfWeek.values().forEach { day ->
            for (i in 0..maxLessonOfDay) {
                val time = Time(day, i)
                if (Util.isTimeFree(this, time, room, group)
                        && Util.isTimeOnTeacherFree(this, time, teacher, groups)) {
                    freeTimes.add(time)
                } else {
                    print("")
                }
            }
        }
        if (freeTimes.isEmpty()) {
            throw Exception("Слишком большое количество предметов. Увеличьте максимальное количество пар в день")
        } else {
            return freeTimes[kotlin.random.Random.nextInt(freeTimes.size)]
        }
    }
}