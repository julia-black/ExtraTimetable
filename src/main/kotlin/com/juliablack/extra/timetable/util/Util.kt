package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.*
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
            if (table[index].size > idxTeacher && table[index][idxTeacher].isNotBlank()) {
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
     * Проверить, свободно ли время
     */
    fun isTimeFree(timeTable: TimetableIndividual, time: Time, room: ClassRoom, group: Group): Boolean {
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
                    timeTable.getClasses()[index].group == group)
                return false
        }
        return true
    }

    fun isTimeOnTeacherFree(timeTable: TimetableIndividual, time: Time, teacher: Teacher, groups: List<Group>): Boolean {
        groups.forEach { group ->
            timeTable.getFullClasses(group).find {
                it.teacher == teacher && it.time == time
            }?.let {
                return false
            }
        }
        return true
    }

    fun isErrorTime(list: List<DayClass>): Boolean {
        if (list.isEmpty() || list[0].classes.isEmpty()) return false
        var time = Time(list[0].dayOfWeek, list[0].classes[0].time)
        list.forEach {
            it.classes.forEachIndexed { idx, elem ->
                if (idx > 0) {
                    if (it.dayOfWeek == time.dayOfWeek && elem.time == time.numberClass) {
                        return true
                    } else {
                        time = Time(list[0].dayOfWeek, list[0].classes[0].time)
                    }
                }
            }
        }
        return false
    }

    fun isErrorTime(list: List<DayClass>, time: Time): Boolean {
        list.find { it.dayOfWeek == time.dayOfWeek }?.classes?.forEach {
            if (time.numberClass == it.time) {
                return true
            }
        }
        return false
    }
}