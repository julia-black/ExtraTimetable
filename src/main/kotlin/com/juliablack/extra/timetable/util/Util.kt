package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.Group
import com.juliablack.extra.timetable.logic.genetic.timetable.Lesson
import com.juliablack.extra.timetable.logic.genetic.timetable.Teacher
import com.juliablack.extra.timetable.logic.genetic.timetable.enums.TypeLesson

object Util {

    fun parseLessons(table: MutableList<MutableList<String>>, idxLecture: Int, idxSeminar: Int, idxLaboratory: Int,
                     lessonNames: List<String>): List<Lesson> {
        var name = ""
        val lessons = mutableListOf<Lesson>()
        lessonNames.forEachIndexed { index, str ->
            if (str.isNotBlank() && !str.contains("//")) {
                name = str
            }
            var type = TypeLesson.LECTURE
            if (table[index][idxSeminar].isNotBlank()) {
                type = TypeLesson.SEMINAR
            } else if (table[index][idxLaboratory].isNotBlank()) {
                type = TypeLesson.LABORATORY
            }
            //todo пока что так, потом придумать как устанавливать эти значения
            val lesson = Lesson(name, type, isNeedComputers = false, isNeedProjector = false)
            if (!lessons.containsLesson(lesson)) {
                lessons.add(lesson)
            }
        }
        return lessons
    }

    fun parseTeachers(table: MutableList<MutableList<String>>, idxNameLesson: Int, column: List<String>): List<Teacher> {
        return listOf()
    }

    fun parseGroups(table: MutableList<MutableList<String>>, idxCountStudentsFree: Int, idxCountStudentsCommerce: Int,
                    groups: List<String>): List<Group> {
        val result = mutableListOf<Group>()
        val regex = Regex("((\\d{3})(\\/\\d{3})?)(\\s?\\((\\d)\\))?") //131, 121/122 - отдельные группы, 131(1) 131 (1) - подгруппа

        groups.forEachIndexed { index, it ->
            regex.findAll(it).iterator().forEach { matchResult ->
                val numGroup = matchResult.groupValues[0]
                var count = 0
                if (!it.contains(",")) { //если это несколько групп, то кол-во не считаем
                    if (table[index][idxCountStudentsFree].isNotBlank()) {
                        count += table[index][idxCountStudentsFree].toDouble().toInt()
                    }
                    if (table[index][idxCountStudentsCommerce].isNotBlank()) {
                        count += table[index][idxCountStudentsCommerce].toDouble().toInt()
                    }
                }
                if (numGroup.contains("(")) { //если это подгруппа
                    val groupFull = matchResult.groupValues[1]
                    val idxThisGroup = result.findGroup(groupFull)
                    if (idxThisGroup != -1) {
                        if (result[idxThisGroup].subGroups.findGroup(numGroup) == -1) {
                            result[idxThisGroup].subGroups.add(Group(numGroup, count))
                        }
                    } else {
                        //если до этого полный группы не было, общее количество студентов неизвестно
                        result.add(Group(groupFull, 0))
                    }
                } else {
                    val idxThisGroup = result.findGroup(numGroup)
                    if (idxThisGroup != -1) { //если такая группа уже добавлена, обновляем кол-во студентов
                        if (count > 0) {
                            result[idxThisGroup].countStudents = count
                        }
                    } else {
                        result.add(Group(numGroup, count))
                    }
                }
            }
        }
        //если у каких-то групп нет отдельных пар, берем кол-во как сумму подгрупп
        result.forEach {
            if (it.countStudents == 0) {
                var count = 0
                it.subGroups.forEach { subGroup ->
                    count += subGroup.countStudents
                }
                it.countStudents = count
            }
        }
        //удаляем пары без кол-ва студентов
        result.removeIf { it.countStudents == 0 }
        return result
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