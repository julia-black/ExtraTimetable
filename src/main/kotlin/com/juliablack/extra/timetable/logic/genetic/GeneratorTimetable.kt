package com.juliablack.extra.timetable.logic.genetic

import com.juliablack.extra.timetable.logic.genetic.common.GeneticAlgorithm
import com.juliablack.extra.timetable.logic.genetic.timetable.*
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.DayOfWeek
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson
import java.util.*


class GeneratorTimetable(
        private var optionalLessonsOfDay: Int? = null,
        private var maxLessonsOfDay: Int? = null) {

    private lateinit var rooms: List<ClassRoom>
    private lateinit var lessons: List<Lesson>
    private lateinit var groups: List<Group>
    private lateinit var teachers: List<Teacher>
    private lateinit var studentProgram: List<GroupProgramm>
    private var geneticAlgorithm: GeneticAlgorithm

    init {
        downloadTimetableFromDB()
        geneticAlgorithm = TimetableGeneticAlg()
    }

    /**
     *  Генерация начальных расписаний в количестве count
     */
    fun generateStartPopulation(countIndividual: Int) {
        maxLessonsOfDay ?: throw Exception("Не задано максимальное количество пар в день")
        val population: MutableList<TimetableIndividual> = mutableListOf()

        for (i in 0 until countIndividual) {
            val individual = TimetableIndividual() //особь - одно расписание
            individual.optionalLessonsOfDay = optionalLessonsOfDay
            individual.groups = groups
            studentProgram.forEach { groupProgram ->
                groupProgram.lessons.forEach { lesson, count ->
                    for (j in 0 until count) {
                        val teacher = getTeacher(lesson)
                                ?: throw Exception("Не найдено преподавателя для предмета $lesson")
                        val room = getRandomRoom(lesson)
                        val time = getRandomTime(individual, room, maxLessonsOfDay!!)
                        individual.addItem(StudentClass(lesson, groupProgram.group, teacher), time, room)
                    }
                }
            }
            population.add(individual)
        }
        geneticAlgorithm.setStartPopulation(population)
    }

    private fun getTeacher(lesson: Lesson): Teacher? = teachers.find {
        it.lessons.contains(lesson)
    }

    private fun getRandomRoom(lesson: Lesson): ClassRoom {
        var room: ClassRoom
        do {
            room = rooms[Random().nextInt(rooms.size)]
        } while (lesson.isNeedComputers && !room.hasComputers)
        return room
    }


    private fun getRandomTime(timeTable: TimetableIndividual, room: ClassRoom, maxCountClasses: Int): Time {
        var time: Time
        do {
            val dayOfWeek = DayOfWeek.getRandomDay()
            val numberClass = Random().nextInt(maxCountClasses)
            time = Time(dayOfWeek, numberClass)
        } while (!isTimeFree(timeTable, time, room))
        return time
    }

    private fun isTimeFree(timeTable: TimetableIndividual, time: Time, room: ClassRoom): Boolean {
        timeTable.getClasses().forEach { studentClass ->
            //если в списке аудиторий уже есть такая
            timeTable.getRooms().getIndexes(room).forEachIndexed { indexRoom, _ ->
                if (timeTable.getTimes().getGen(indexRoom) == time //если время совпадает, значит аудитория в это время занята
                        && timeTable.getClasses()[indexRoom] != studentClass)
                    return false
            }
        }
        return true
    }

    /**
     * Генерация расписания (основной процесс)
     */
    fun generateTimetable() {
        geneticAlgorithm.generationPopulation()
        geneticAlgorithm.crossover()
        geneticAlgorithm.generationPopulation()
    }

    /**
     * Загрузка из БД данных для составляения расписания
     */
    private fun downloadTimetableFromDB() {
        //todo:здесь будет загрузка из базы учителей, пар, групп, классов и т.д.

        val lesson = Lesson("Программирование", TypeLesson.LECTURE, false)
        val lesson1 = Lesson("Программирование", TypeLesson.PRACTICE, true)
        val lesson2 = Lesson("Анализ данных", TypeLesson.LECTURE, false)
        val lesson3 = Lesson("Математический анализ", TypeLesson.LECTURE, false)

        lessons = listOf(lesson, lesson1, lesson2, lesson3)

        val teacher1 = Teacher("Иванов И.И.", listOf(lesson, lesson1))
        val teacher2 = Teacher("Степанов С.С.", listOf(lesson2))
        val teacher3 = Teacher("Попова М.В.", listOf(lesson3))

        teachers = listOf(teacher1, teacher2, teacher3)

        val classRoom100 = ClassRoom(100, 12, 30, false, true)
        val classRoom101 = ClassRoom(101, 12, 50, false, true)
        val classRoom222 = ClassRoom(222, 12, 20, true, false)

        rooms = listOf(classRoom100, classRoom101, classRoom222)
        groups = listOf(Group(121, 20), Group(122, 23))

        studentProgram = listOf(
                GroupProgramm(groups[0], mapOf(Pair(lesson, 1), Pair(lesson1, 2), Pair(lesson2, 1), Pair(lesson3, 3))),
                GroupProgramm(groups[1], mapOf(Pair(lesson, 1), Pair(lesson1, 2), Pair(lesson3, 3)))
        )
    }
}