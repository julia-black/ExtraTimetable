package com.juliablack.extra.timetable.logic.genetic

import com.google.gson.Gson
import com.juliablack.extra.timetable.logic.db.Database
import com.juliablack.extra.timetable.logic.db.DbContract
import com.juliablack.extra.timetable.logic.genetic.common.GeneticAlgorithm
import com.juliablack.extra.timetable.logic.genetic.timetable.*
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.DayOfWeek
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson
import io.reactivex.Observable
import org.nield.rxkotlinjdbc.select
import java.io.PrintWriter
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
    fun generateTimetable(): Observable<Timetable> {
        for (i in 0 until COUNT_CYCLE_ALGORITHM) {
            geneticAlgorithm.generationPopulation()
            geneticAlgorithm.crossover()
            geneticAlgorithm.mutationAll(0.5) //вероятность мутации
        }
        return Observable.just(Timetable(geneticAlgorithm.getBestIndividual() as TimetableIndividual))
    }

    fun saveTimetable(timeTable: Timetable) {
        val jsonTimetable = Gson().toJson(timeTable)
        PrintWriter("timetable.json", "UTF-8").use { file ->
            file.write(jsonTimetable)
        }
    }

    /**
     * Загрузка из БД данных для составляения расписания
     */
    private fun downloadTimetableFromDB() {
        Database.getGroups().toList().subscribe { it -> groups = it }
        Database.getLessons().toList().subscribe { it -> lessons = it }
        Database.getTeachers().subscribe { res ->
            val idTeacher = res.getInt(DbContract.ID_TEACHER)
            println(teachers.toString())
            teachers.find { teacher -> teacher.id == idTeacher }?.let {
                teachers.find { teacher -> teacher.id == idTeacher }?.lessons!!.add(
                        Lesson(res.getString(DbContract.NAME_LESSON),
                                if (res.getString(DbContract.TYPE_LESSONS) == "Лекция")
                                    TypeLesson.LECTURE
                                else TypeLesson.PRACTICE,
                                res.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                                res.getInt(DbContract.IS_NEED_PROJECTOR) == 1))
                return@subscribe
            }
            teachers.add(Teacher(res.getInt(DbContract.ID_TEACHER), res.getString(DbContract.NAME), mutableListOf(
                    Lesson(res.getString(DbContract.NAME_LESSON),
                            if (res.getString(DbContract.TYPE_LESSONS) == "Лекция")
                                TypeLesson.LECTURE
                            else TypeLesson.PRACTICE,
                            res.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                            res.getInt(DbContract.IS_NEED_PROJECTOR) == 1)))
            )
        }
        Database.getRooms().toList().subscribe { it -> rooms = it }
        Database.getGroupsProgram().subscribe { res ->
            val group = res.getInt(DbContract.NUMBER_GROUP)
            studentProgram.find { it.group.number == group }?.let {
                it.lessons[Lesson(res.getString(DbContract.NAME_LESSON),
                        if (res.getString(DbContract.TYPE_LESSONS) == "Лекция")
                            TypeLesson.LECTURE
                        else TypeLesson.PRACTICE,
                        res.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                        res.getInt(DbContract.IS_NEED_PROJECTOR) == 1)] =
                        res.getInt(DbContract.COUNT_IN_WEEK)
                return@subscribe
            }
            val groupProgram = Pair(
                    Lesson(res.getString(DbContract.NAME_LESSON),
                            if (res.getString(DbContract.TYPE_LESSONS) == "Лекция")
                                TypeLesson.LECTURE
                            else TypeLesson.PRACTICE,
                            res.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                            res.getInt(DbContract.IS_NEED_PROJECTOR) == 1),
                    res.getInt(DbContract.COUNT_IN_WEEK))

            groups.find { it.number == group }
                    ?: throw Exception("В таблице групп не найдена группа $group из групповой программы")

            studentProgram.add(GroupProgramm(groups.find { it.number == group }!!,
                    mutableMapOf(groupProgram)))
        }
    }

    companion object {

        const val COUNT_CYCLE_ALGORITHM = 100

        private lateinit var rooms: List<ClassRoom>
        private lateinit var lessons: List<Lesson>
        private lateinit var groups: List<Group>
        private var teachers: MutableList<Teacher> = mutableListOf()
        private var studentProgram: MutableList<GroupProgramm> = mutableListOf()

        fun generationTriple(lesson: Lesson, group: Group, individual: TimetableIndividual, maxLessonsOfDay: Int): Triple<StudentClass, Time, ClassRoom> {
            val teacher = getTeacher(lesson)
                    ?: throw Exception("Не найдено преподавателя для предмета ${lesson.name}")

            val room = getRandomRoom(lesson)
            val time = getRandomTime(individual, room, teacher, maxLessonsOfDay, group)
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

        private fun getRandomTime(timeTable: TimetableIndividual, room: ClassRoom, teacher: Teacher, maxCountClasses: Int, group: Group)
                : Time {
            var time: Time
            do {
                val dayOfWeek = DayOfWeek.getRandomDay()
                val numberClass = Random().nextInt(maxCountClasses)
                time = Time(dayOfWeek, numberClass)
            } while (!isTimeFree(timeTable, time, room, group) && !isTimeOnTeacherFree(timeTable, time, teacher))
            return time
        }

        /**
         * Проверить, свободно ли время
         */
        private fun isTimeFree(timeTable: TimetableIndividual, time: Time, room: ClassRoom, group: Group): Boolean {
            timeTable.getClasses().forEach { studentClass ->
                //если в списке аудиторий уже есть такая
                timeTable.getRooms().getIndexes(room).forEachIndexed { indexRoom, _ ->
                    if (timeTable.getTimes().getGen(indexRoom) == time //если время совпадает, значит аудитория в это время занята
                            && timeTable.getClasses()[indexRoom] != studentClass)
                        return false
                }
            }
            //Проверяем, есть ли в хромосоме времени это время с данной группой
            timeTable.getTimes().getGenom().forEachIndexed { index, gene ->
                if (gene == time &&
                        timeTable.getClasses().get(index).group == group)
                    return false
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