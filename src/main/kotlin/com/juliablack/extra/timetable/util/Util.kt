package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.Group
import com.juliablack.extra.timetable.logic.genetic.timetable.GroupProgram
import com.juliablack.extra.timetable.logic.genetic.timetable.Lesson
import com.juliablack.extra.timetable.logic.genetic.timetable.Teacher
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

object Util {
    fun parseExcel(file: File, lessons: List<Lesson>, groups: List<Group>,
                   teachers: List<Teacher>, studentProgram: List<GroupProgram>) {
        val workbook: Workbook = XSSFWorkbook(file)
        val sheet = workbook.getSheetAt(0)
        val iterator = sheet.rowIterator()
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
        var idxTeacher = 16 //todo пока нет названия столбца
        val table = mutableListOf<MutableList<String>>()
        while (iterator.hasNext()) {
            val row = iterator.next()
            val cells = mutableListOf<String>()
            row.forEachIndexed { index, cell ->
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
                                    if (idxCountStudentsFree < 0) {
                                        idxCountStudentsFree = cell.columnIndex
                                    }
                                }
                                cell.stringCellValue.containsIgnoreCase("Коммерч") -> {
                                    if (idxCountStudentsCommerce < 0)
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
                        else -> {
                        }
                    }
                }
            }
            if (cells.isNotEmpty()) {
                table.add(cells)
            }
        }
        println(table)

        val allGroups = table.getColumn(idxGroup)

        val groups = mutableListOf<Group>()

    }

    private fun parseGroups(table: MutableList<MutableList<String>>) {
        val regex = "(\\d{3})(\\/\\d{3})?(\\s?\\((\\d)\\))?"
        //131, 121/122 - отдельные группы, 131(1) 131 (1) - подгруппа
        //todo придумать как разделять подгруппы одной группы
    }

    private fun MutableList<MutableList<String>>.getColumn(idxColumn: Int): List<String> {
        val result = mutableListOf<String>()
        forEach { row ->
            result.add(row[idxColumn])
        }
        return result
    }
}