package com.juliablack.extra.timetable.util

import com.juliablack.extra.timetable.logic.genetic.timetable.*
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File


//fun File.parseExcel(rooms: List<ClassRoom>, lessons: List<Lesson>, groups: List<Group>,
//                    teachers: List<Teacher>, studentProgram: List<GroupProgram>) {
fun File.parseExcel() {
    val workbook: Workbook = XSSFWorkbook(this)
    val sheet = workbook.getSheetAt(0)
    val iterator = sheet.rowIterator()
    var idxStartRow = 0
    var idxName = -1
    var idxSemester = -1
    var idxCountStudents = -1
    var idxGroup = -1
    var idxLecture = -1
    var idxSeminar = -1
    var idxLaboratory = -1
    var idxCountInWeek = -1
    var idxTeacher = 16
    val table = mutableListOf<MutableList<String>>(mutableListOf())
    while (iterator.hasNext()) {
        val row = iterator.next()
        val cells = mutableListOf<String>()
        row.forEachIndexed { index, cell ->
            if (row.rowNum <= idxStartRow) {
                when (cell.cellType) {
                    CellType.STRING -> {
                        when {
                            cell.stringCellValue.containsIgnoreCase("Наименование дисциплины") -> {
                                idxStartRow = row.rowNum
                                idxName = cell.columnIndex
                            }
                            cell.stringCellValue.containsIgnoreCase("Семестр") -> {
                                idxSemester = cell.columnIndex
                            }
                            cell.stringCellValue.containsIgnoreCase("Количество студентов") -> { //todo разделение на бюджет и коммерцию
                                idxCountStudents = cell.columnIndex
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
                    else -> {}
                }
            }
        }
        table.add(cells)
    }
    println(table)
}

fun String.containsIgnoreCase(string: String): Boolean =
        this.toLowerCase().contains(string.toLowerCase())