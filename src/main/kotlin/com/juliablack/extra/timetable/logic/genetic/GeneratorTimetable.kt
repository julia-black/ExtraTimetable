package com.juliablack.extra.timetable.logic.genetic

import com.juliablack.extra.timetable.logic.db.DbContract
import com.juliablack.extra.timetable.logic.db.db
import com.juliablack.extra.timetable.logic.genetic.common.GeneticAlgorithm
import com.juliablack.extra.timetable.logic.genetic.timetable.*
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.DayOfWeek
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson
import org.nield.rxkotlinjdbc.select
import java.util.*

class GeneratorTimetable(
        private var optionalLessonsOfDay: Int? = null,
        private var maxLessonsOfDay: Int? = null) {

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
            val individual = TimetableIndividual(maxLessonsOfDay!!) //особь - одно расписание
            individual.optionalLessonsOfDay = optionalLessonsOfDay
            individual.groups = groups
            studentProgram.forEach { groupProgram ->
                groupProgram.lessons.forEach { lesson, count ->
                    for (j in 0 until count) {
                        val triple = generationTriple(lesson, groupProgram.group, individual, maxLessonsOfDay!!)
                        individual.addItem(triple.first, triple.second, triple.third)
                    }
                }
            }
            population.add(individual)
        }
        geneticAlgorithm.setStartPopulation(population)
    }

    /**
     * Генерация расписания (основной процесс)
     */
    fun generateTimetable() {
        geneticAlgorithm.generationPopulation()
        geneticAlgorithm.crossover()
        geneticAlgorithm.mutationAll(0.5) //вероятность мутации
        geneticAlgorithm.generationPopulation()
    }

    private fun getTeachers() {
        db.select("SELECT * FROM ${DbContract.TEACHER_TABLE}")
                .toObservable { Teacher(it.getInt(DbContract.ID), it.getString(DbContract.NAME), listOf()) }
                .toList()
                .subscribe { list ->
                    teachers = list
                }
    }

    private fun getGroups() {
        db.select("SELECT * FROM ${DbContract.GROUP_TABLE}")
                .toObservable { Group(it.getInt(DbContract.NUMBER_GROUP), it.getString(DbContract.FACULTY), it.getInt(DbContract.COUNT)) }
                .toList()
                .subscribe { it ->
                    groups = it
                }
    }

    private fun getLessons() {
        db.select("SELECT * FROM ${DbContract.LESSON_TABLE}")
                .toObservable {
                    Lesson(it.getString(DbContract.NAME),
                            if (it.getString(DbContract.TYPE_LESSONS) == "Лекция")
                                TypeLesson.LECTURE
                            else TypeLesson.PRACTICE,
                            it.getInt(DbContract.IS_NEED_COMPUTERS) == 1)
                }
                .toList()
                .subscribe { it ->
                    lessons = it
                }
    }

    /**
     * Загрузка из БД данных для составляения расписания
     */
    private fun downloadTimetableFromDB() {
        getGroups()
        getLessons()
        getTeachers()

         val lesson = Lesson("Программирование", TypeLesson.LECTURE, false)
         val lesson1 = Lesson("Программирование", TypeLesson.PRACTICE, true)
         val lesson2 = Lesson("Анализ данных", TypeLesson.LECTURE, false)
         val lesson3 = Lesson("Математический анализ", TypeLesson.LECTURE, false)
         lessons = listOf(lesson, lesson1, lesson2, lesson3)

        val teacher1 = Teacher(0, "Иванов И.И.", listOf(lesson, lesson1))
        val teacher2 = Teacher(1, "Степанов С.С.", listOf(lesson2))
        val teacher3 = Teacher(2, "Попова М.В.", listOf(lesson3))

          val classRoom100 = ClassRoom(100, 12, 30, false, true)
          val classRoom101 = ClassRoom(101, 12, 50, false, true)
          val classRoom222 = ClassRoom(222, 12, 20, true, false)

          rooms = listOf(classRoom100, classRoom101, classRoom222)

          studentProgram = listOf(
                  GroupProgramm(groups[0], mapOf(Pair(lesson, 1), Pair(lesson1, 2), Pair(lesson2, 1), Pair(lesson3, 3))),
                  GroupProgramm(groups[1], mapOf(Pair(lesson, 1), Pair(lesson1, 2), Pair(lesson3, 3)))
          )
    }

    companion object {
        private lateinit var rooms: List<ClassRoom>
        private lateinit var lessons: List<Lesson>
        private lateinit var groups: List<Group>
        private lateinit var teachers: List<Teacher>
        private lateinit var studentProgram: List<GroupProgramm>

        fun generationTriple(lesson: Lesson, group: Group, individual: TimetableIndividual, maxLessonsOfDay: Int): Triple<StudentClass, Time, ClassRoom> {
            val teacher = getTeacher(lesson)
                    ?: throw Exception("Не найдено преподавателя для предмета ${lesson.name}")

            val room = getRandomRoom(lesson)
            val time = getRandomTime(individual, room, teacher, maxLessonsOfDay)
            return Triple(StudentClass(lesson, group, teacher), time, room)
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


        private fun getRandomTime(timeTable: TimetableIndividual, room: ClassRoom, teacher: Teacher, maxCountClasses: Int)
                : Time {
            var time: Time
            do {
                val dayOfWeek = DayOfWeek.getRandomDay()
                val numberClass = Random().nextInt(maxCountClasses)
                time = Time(dayOfWeek, numberClass)
            } while (!isTimeFree(timeTable, time, room) && !isTimeOnTeacherFree(timeTable, time, teacher))
            return time
        }

        /**
         * Проверить, свободно ли время
         */
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

        private fun isTimeOnTeacherFree(timeTable: TimetableIndividual, time: Time, teacher: Teacher): Boolean {
            groups.forEach { group ->
                timeTable.getFullClasses(group).find {
                    it.teacher == teacher && it.time == time
                }?.let {
                    return false
                }
            }
            return true
        }
    }
}