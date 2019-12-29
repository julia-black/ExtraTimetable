package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.Group
import com.juliablack.extra.timetable.logic.genetic.timetable.GroupProgram
import com.juliablack.extra.timetable.logic.genetic.timetable.Lesson
import com.juliablack.extra.timetable.logic.genetic.timetable.Teacher
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson

object Util {

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
                lessons.put(index, lesson)
            }
            //Добавляем преподавателя
            if (table[index][idxTeacher].isNotBlank()) {
                teacherName = table[index][idxTeacher]
            }
            val idxTeacherInList = teachers.indexOfFirst { it.name == teacherName }
            if (idxTeacherInList != -1) { //если такой преподаватель уже записан
                teachers[idxTeacherInList].lessons.add(lesson) //добавляем ему эту лекцию
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
                    val idxThisGroup = resultGroups.findGroup(groupFull)
                    if (idxThisGroup != -1) {
                        if (resultGroups[idxThisGroup].subGroups.findGroup((numGroup)) == -1) {
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
                    val idxThisGroup = resultGroups.findGroup(numGroup)
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
        return Pair(resultGroups, resultProgram)
    }

    private fun addLessonsInProgram(lessonsWithIdx: Map<Int, Lesson>, index: Int, numGroup: String, resultProgram: MutableList<GroupProgram>,
                            countInWeek: Int, idProgram: Int): Boolean {
        val lesson = lessonsWithIdx.findLesson(index) ?: throw Exception("Не удалось найти лекцию")
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
}