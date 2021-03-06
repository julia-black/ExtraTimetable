package com.juliablack.extra.timetable.logic.genetic

import com.google.gson.Gson
import com.juliablack.extra.timetable.logic.db.Database
import com.juliablack.extra.timetable.logic.db.DbContract
import com.juliablack.extra.timetable.logic.genetic.common.GeneticAlgorithm
import com.juliablack.extra.timetable.logic.genetic.timetable.*
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.CrossoverType
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.MutationType
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson
import com.juliablack.extra.timetable.util.*
import io.reactivex.Observable
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.PrintWriter
import java.util.*

class GeneratorTimetable(
        private val optionalLessonsOfDay: Int? = null,
        private val maxLessonsOfDay: Int? = null,
        private val file: File? = null) {

    companion object {
        private var rooms: MutableList<ClassRoom> = mutableListOf()
        private var lessons: MutableList<Lesson> = mutableListOf()
        private var groups: MutableList<Group> = mutableListOf()
        private var teachers: MutableList<Teacher> = mutableListOf()
        private var studentProgram: MutableList<GroupProgram> = mutableListOf()

        fun generationTriple(lesson: Lesson, group: Group, individual: TimetableIndividual)
                : Triple<StudentClass, Time, ClassRoom> {
            val teacher = getTeacher(lesson)
                    ?: throw Exception("Не найдено преподавателя для предмета ${lesson.name}")

            val room = getRandomRoom(lesson)
            val time = getRandomTime(individual, room, teacher, group)
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

        private fun getRandomTime(timeTable: TimetableIndividual, room: ClassRoom, teacher: Teacher, group: Group): Time =
                timeTable.getRandomFreeTime(room, teacher, group, groups)
    }

    private var geneticAlgorithm: GeneticAlgorithm

    init {
        if (file == null) {
            downloadTimetableFromDB()
        } else {
            downloadTimetableFromFile(file)
        }
        geneticAlgorithm = TimetableGeneticAlg()
    }

    /**
     *  Генерация начальных расписаний в количестве count
     */
    fun generateStartPopulation(countIndividual: Int, callback: () -> Unit) {
        maxLessonsOfDay ?: throw Exception("Не задано максимальное количество пар в день")
        val population: MutableList<TimetableIndividual> = mutableListOf()

        for (i in 0 until countIndividual) {
            val individual = TimetableIndividual(maxLessonsOfDay) //особь - одно расписание
            individual.optionalLessonsOfDay = optionalLessonsOfDay
            individual.groups = groups
            studentProgram.forEach { groupProgram ->
                groupProgram.lessons.forEach { (lesson, count) ->
                    for (j in 0 until count) {
                        val group = groups.findGroup(groupProgram.numGroup)
                                ?: throw Exception("Не удалось найти группу из групповой программы")
                        val triple = generationTriple(lesson, group, individual)
                        individual.addItem(triple.first, triple.second, triple.third)
                    }
                }
            }
            population.add(individual)
            callback.invoke()
        }
        geneticAlgorithm.setStartPopulation(population)
    }

    fun testExperiment(callback: () -> Unit): Observable<Pair<Timetable, Triple<Float, Float, Float>>> {
        val bestTimetable: TimetableIndividual?
        var mean = 0f
        for (i in 0 until Settings.countCycle) {
            geneticAlgorithm.generationPopulation()
            if (i == 0L) {
                mean = geneticAlgorithm.getMeanFitnessFunction() //первоначальное среднее значение
            }
            geneticAlgorithm.crossover(CrossoverType.PANMIXIA)
            geneticAlgorithm.mutation(MutationType.LARGE_MUTATION)
            callback.invoke()
        }

        bestTimetable = geneticAlgorithm.getBestIndividual() as TimetableIndividual
        val bestFitness = bestTimetable.calculateFitnessFunction().toFloat()
        val efficiency = getEfficiency(mean, bestFitness)
        println("Mean of first population: $mean, Best: $bestFitness, Efficiency: $efficiency%")

        return Observable.just(Pair(Timetable(bestTimetable), Triple(mean, bestFitness, efficiency)))
    }

    fun saveTimetable(timeTable: Timetable) {
        val jsonTimetable = Gson().toJson(timeTable)
        PrintWriter("timetable.json", "UTF-8").use { file ->
            file.write(jsonTimetable)
        }
    }

    private fun getEfficiency(mean: Float, bestFitness: Float): Float {
        val result = 100 * bestFitness / mean
        return 100 - result
    }

    @Deprecated("This function is out of date. Use download from Excel")
    fun downloadTimetableFromDB() {
        Database.getGroups().toList().subscribe { it -> groups = it }
        Database.getLessons().toList().subscribe { it -> lessons = it }
        Database.getTeachers().subscribe { res ->
            val idTeacher = res.getInt(DbContract.ID_TEACHER)
            teachers.find { teacher -> teacher.id == idTeacher }?.let {
                teachers.find { teacher -> teacher.id == idTeacher }?.lessons!!.add(
                        Lesson(res.getString(DbContract.NAME_LESSON),
                                if (res.getString(DbContract.TYPE_LESSONS) == "Лекция")
                                    TypeLesson.LECTURE
                                else TypeLesson.LABORATORY,
                                res.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                                res.getInt(DbContract.IS_NEED_PROJECTOR) == 1))
                return@subscribe
            }
            teachers.add(Teacher(res.getInt(DbContract.ID_TEACHER), res.getString(DbContract.NAME), mutableListOf(
                    Lesson(res.getString(DbContract.NAME_LESSON),
                            if (res.getString(DbContract.TYPE_LESSONS) == "Лекция")
                                TypeLesson.LECTURE
                            else TypeLesson.LABORATORY,
                            res.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                            res.getInt(DbContract.IS_NEED_PROJECTOR) == 1)))
            )
        }
        Database.getGroupsProgram().subscribe { res ->
            val group = res.getString(DbContract.NUMBER_GROUP)
            studentProgram.find { it.numGroup == group }?.let {
                it.lessons[Lesson(res.getString(DbContract.NAME_LESSON),
                        if (res.getString(DbContract.TYPE_LESSONS) == "Лекция")
                            TypeLesson.LECTURE
                        else TypeLesson.LABORATORY,
                        res.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                        res.getInt(DbContract.IS_NEED_PROJECTOR) == 1)] =
                        res.getInt(DbContract.COUNT_IN_WEEK)
                return@subscribe
            }
            val groupProgram = Pair(
                    Lesson(res.getString(DbContract.NAME_LESSON),
                            if (res.getString(DbContract.TYPE_LESSONS) == "Лекция")
                                TypeLesson.LECTURE
                            else TypeLesson.LABORATORY,
                            res.getInt(DbContract.IS_NEED_COMPUTERS) == 1,
                            res.getInt(DbContract.IS_NEED_PROJECTOR) == 1),
                    res.getInt(DbContract.COUNT_IN_WEEK))

            groups.find { it.number == group }
                    ?: throw Exception("В таблице групп не найдена группа $group из групповой программы")

            studentProgram.add(GroupProgram(group, mutableMapOf(groupProgram)))
        }
        getRoomsFromDB()
    }

    @Throws(Exception::class)
    private fun downloadTimetableFromFile(file: File) {
        getRoomsFromDB()
        parseExcel(file, Settings.isFirstSemester)
    }

    @Throws(Exception::class)
    private fun parseExcel(file: File, isFirstSemester: Boolean = true) {
        var idxStartRow = Int.MAX_VALUE
        var idxName = -1
        var idxSemester = -1
        var idxCountStudentsFree = -1
        var idxCountStudentsCommerce = -1
        var idxGroup = -1
        var idxLecture = -1
        var idxSeminar = -1
        var idxLaboratory = -1
        var idxCountInWeek = -1
        var idxTeacher = -1
        val table = mutableListOf<MutableList<String>>()
        val workbook: Workbook = XSSFWorkbook(file)

        for (i in 0 until 13) {
            val sheet = workbook.getSheetAt(i)
            val iterator = sheet.rowIterator()
            while (iterator.hasNext()) {
                val row = iterator.next()
                val cells = mutableListOf<String>()
                row.forEach { cell ->
                    if (row.rowNum <= idxStartRow) {
                        when (cell.cellType) {
                            CellType.STRING -> {
                                when {
                                    cell.stringCellValue.containsIgnoreCase("Наименование дисциплины") -> {
                                        idxName = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Семестр") -> {
                                        idxSemester = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Бюдж") -> {
                                        idxCountStudentsFree = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Коммерч") -> {
                                        idxCountStudentsCommerce = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Группа") -> {
                                        idxGroup = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Лекции") -> {
                                        idxLecture = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Семинар") -> {
                                        idxSeminar = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Лаборатор") -> {
                                        idxStartRow = row.rowNum
                                        idxLaboratory = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Нагрузка в неделю") -> {
                                        idxCountInWeek = cell.columnIndex
                                    }
                                    cell.stringCellValue.containsIgnoreCase("Преподаватель") -> {
                                        idxTeacher = cell.columnIndex
                                    }
                                }
                            }
                            else -> {
                            }
                        }
                    } else {
                        when (cell.cellType) {
                            CellType.STRING -> cells.add(cell.stringCellValue)
                            CellType.FORMULA -> cells.add((cell as XSSFCell).rawValue)
                            CellType.NUMERIC -> cells.add(cell.numericCellValue.toString())
                            CellType.BLANK -> cells.add("")
                            else -> cells.add("")
                        }
                    }
                }
                try {
                    if (cells.isNotEmpty() && Util.isCorrectSemester(cells, idxSemester, isFirstSemester) &&
                            cells.size >= idxTeacher && cells[idxCountInWeek].isNotBlank()) {
                        table.add(cells)
                    }
                } catch (e: IndexOutOfBoundsException) {
                    println("")
                }
            }
        }

        val pair = Util.parseLessonsAndTeachers(table, idxSeminar, idxLaboratory, idxTeacher, table.getColumn(idxName))
        val lessonWithIdx = pair.first
        lessons.addAll(lessonWithIdx.values.toList())
        teachers.addAll(pair.second)

        val pairGroups = Util.parseGroupsAndProgram(table, idxCountStudentsFree, idxCountStudentsCommerce, idxCountInWeek,
                table.getColumn(idxGroup), lessonWithIdx)
        groups.addAll(pairGroups.first)
        studentProgram.addAll(pairGroups.second)
    }

    private fun getRoomsFromDB() {
        Database.getRooms().toList().subscribe { it -> rooms = it }
    }
}