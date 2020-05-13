package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.*
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson

object Util {

    private const val ANSI_GREEN = "\u001B[32m"
    private const val ANSI_RESET = "\u001b[0m"

    fun parseLessonsAndTeachers(table: MutableList<MutableList<String>>, idxSeminar: Int, idxLaboratory: Int,
                                idxTeacher: Int, lessonNames: List<String>): Pair<Map<Int, Lesson>, MutableList<Teacher>> {
        var title = ""
        var teacherName = ""
        var idTeacher = 0
        val lessons = mutableMapOf<Int, Lesson>()
        val teachers = mutableListOf<Teacher>()
        lessonNames.forEachIndexed { index, str ->
            if (str.isNotBlank() && !str.contains("//")) {
                title = str
            }
            var type = TypeLesson.LECTURE
            if (table[index][idxSeminar].isNotBlank()) {
                type = TypeLesson.SEMINAR
            } else if (table[index][idxLaboratory].isNotBlank()) {
                type = TypeLesson.LABORATORY
            }
            val lesson = Lesson(title, type, type == TypeLesson.LABORATORY, isNeedProjector = false)
            if (!lessons.containsLesson(lesson)) {
                lessons[index] = lesson
            }
            //Добавляем преподавателя
            if (table[index].size > idxTeacher && table[index][idxTeacher].isNotBlank()) {
                teacherName = table[index][idxTeacher]
            }
            val idxTeacherInList = teachers.indexOfFirst { it.name == teacherName }
            if (idxTeacherInList != -1) { //если такой преподаватель уже записан
                if (!teachers[idxTeacherInList].lessons.contains(lesson)) {
                    teachers[idxTeacherInList].lessons.add(lesson) //добавляем ему эту лекцию
                }
            } else {
                teachers.add(Teacher(idTeacher, teacherName, mutableListOf(lesson)))
                idTeacher++
            }
        }
        return Pair(lessons, teachers)
    }

    @Throws(Exception::class)
    fun parseGroupsAndProgram(table: MutableList<MutableList<String>>, idxCountStudentsFree: Int, idxCountStudentsCommerce: Int,
                              idxCountInWeek: Int, groups: List<String>, lessonsWithIdx: Map<Int, Lesson>): Pair<List<Group>, MutableList<GroupProgram>> {
        var idProgram = 0
        val resultGroups = mutableListOf<Group>()
        val resultProgram = mutableListOf<GroupProgram>()
        val regex = Regex("((\\d{3})(\\/\\d{3})?)(\\s?\\((\\d)\\))?") //131, 121/122 - отдельные группы, 131(1) 131 (1) - подгруппа

        groups.forEachIndexed { index, it ->
            regex.findAll(it).iterator().forEach { matchResult ->
                val numGroup = matchResult.groupValues[0]
                var count = 0
                var countInWeek = 0
                if (!it.contains(",")) { //если это несколько групп, то кол-во не считаем
                    if (table[index][idxCountStudentsFree].isNotBlank()) {
                        count += table[index][idxCountStudentsFree].toDouble().toInt()
                    }
                    if (table[index][idxCountStudentsCommerce].isNotBlank()) {
                        count += table[index][idxCountStudentsCommerce].toDouble().toInt()
                    }
                }
                countInWeek = table[index][idxCountInWeek].toDouble().toInt()
                if (numGroup.contains("(")) { //если это подгруппа
                    val groupFull = matchResult.groupValues[1]
                    val idxThisGroup = resultGroups.findIdxGroup(groupFull)
                    if (idxThisGroup != -1) {
                        if (resultGroups[idxThisGroup].subGroups.findIdxGroup((numGroup)) == -1) {
                            resultGroups[idxThisGroup].subGroups.add(Group(numGroup, count))
                        }
                        if (addLessonsInProgram(lessonsWithIdx, index, groupFull, resultProgram, countInWeek, idProgram)) {
                            idProgram++
                        }
                    } else {
                        //если до этого полный группы не было, общее количество студентов неизвестно
                        resultGroups.add(Group(groupFull, 0))

                        if (addLessonsInProgram(lessonsWithIdx, index, groupFull, resultProgram, countInWeek, idProgram)) {
                            idProgram++
                        }
                    }
                } else {
                    val idxThisGroup = resultGroups.findIdxGroup(numGroup)
                    if (idxThisGroup != -1) { //если такая группа уже добавлена, обновляем кол-во студентов
                        if (count > 0) {
                            resultGroups[idxThisGroup].countStudents = count
                            if (addLessonsInProgram(lessonsWithIdx, index, numGroup, resultProgram, countInWeek, idProgram)) {
                                idProgram++
                            }
                        }
                    } else {
                        resultGroups.add(Group(numGroup, count))
                        if (addLessonsInProgram(lessonsWithIdx, index, numGroup, resultProgram, countInWeek, idProgram)) {
                            idProgram++
                        }
                    }
                }
            }
        }
        //если у каких-то групп нет отдельных пар, берем кол-во как сумму подгрупп
        resultGroups.forEach {
            if (it.countStudents == 0) {
                var count = 0
                it.subGroups.forEach { subGroup ->
                    count += subGroup.countStudents
                }
                it.countStudents = count
            }
        }
        resultGroups.removeIf { it.countStudents == 0 }
        resultProgram.removeIf { program ->
            resultGroups.findGroup(program.numGroup) == null
        }
        return Pair(resultGroups, resultProgram)
    }

    private fun addLessonsInProgram(lessonsWithIdx: Map<Int, Lesson>, index: Int, numGroup: String, resultProgram: MutableList<GroupProgram>,
                                    countInWeek: Int, idProgram: Int): Boolean {
        val lesson = lessonsWithIdx.findLesson(index) ?: return false
        val idxProgram = resultProgram.indexOfFirst { it.numGroup == numGroup }
        return if (idxProgram != -1) { //Если в программе уже есть эта группа
            resultProgram[idxProgram].lessons[lesson] = countInWeek
            false
        } else {
            val lessons = mutableMapOf<Lesson, Int>()
            lessons[lesson] = countInWeek
            resultProgram.add(idProgram, GroupProgram(numGroup, lessons))
            true
        }
    }

    fun isCorrectSemester(cells: MutableList<String>, idxSemester: Int, isFirstSemester: Boolean): Boolean {
        val regex = Regex("(\\d)")
        regex.find(cells[idxSemester])?.groupValues?.forEach { semester ->
            if (semester.toDouble().toInt() % 2 == 1 != isFirstSemester) {
                return false
            }
        }
        return true
    }

    /**
     * Проверка имеет ли пара уже созданные соседние пары
     */
    fun hasNearbyTime(timeTable: TimetableIndividual, time: Time): Boolean {
        timeTable.getTimes().getGenom().forEach {
            val currentTime = it as Time
            if (currentTime.dayOfWeek == time.dayOfWeek && (currentTime.numberClass == time.numberClass + 1 ||
                            (time.numberClass != 0 && currentTime.numberClass == time.numberClass - 1))) {
                return true
            }
        }
        return false
    }

    fun isTimeFreeForGroupAndTeacher(timeTable: TimetableIndividual, time: Time, room: ClassRoom, group: Group,
                                     teacher: Teacher): Boolean {
        timeTable.getClasses().forEachIndexed { index, studentClass ->
            //если в списке аудиторий уже есть такая
            timeTable.getRooms().getIndexes(room).forEachIndexed { indexRoom, _ ->
                if (timeTable.getTimes().getGen(indexRoom) == time //если время совпадает, значит аудитория в это время занята
                        && timeTable.getClasses()[indexRoom] != studentClass)
                    return false
            }
        }
        //Проверяем, есть ли в хромосоме времени это время с данной группой или с этим преподавателем
        timeTable.getTimes().getGenom().forEachIndexed { index, gene ->
            if (gene == time &&
                    (timeTable.getClasses()[index].group == group
                            || timeTable.getClasses()[index].teacher == teacher))
                return false
        }
        return true
    }

    fun printFitnessFunctions(population: MutableList<TimetableIndividual>) {
        population.forEach {
            print("${it.fitnessFunction} ")
        }
        println()
    }

    fun showResult(results: MutableList<Triple<Float, Float, Float>>) {
        var mean = 0f
        var bestFitness = 0f
        var efficiency = 0f
        results.forEach {
            mean += it.first
            bestFitness += it.second
            efficiency += it.third
        }
        mean /= results.size.toFloat()
        bestFitness /= results.size.toFloat()
        efficiency /= results.size.toFloat()
        println("All results: Mean of first population: $mean, Best: $bestFitness, Efficiency: $efficiency%")
    }

    fun printTime(action: String, time: Long) {
        println("${ANSI_GREEN}$action completed for: ${time / 1000.0} sec. (${time / 1000.0 / 60.0} min.)${ANSI_RESET}")
    }
}