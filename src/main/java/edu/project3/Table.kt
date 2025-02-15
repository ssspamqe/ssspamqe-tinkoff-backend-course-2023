package edu.project3

import jakarta.validation.constraints.NotEmpty
import kotlin.math.max

data class Table(
    @NotEmpty
    private var rows: List<Map<String, String?>>
) {

    public val tableRows
        get() = rows


    public val columns: List<String>

    val size: Int
        get() = rows.size

    init {
        val keys = rows[0].keys.toMutableSet()
        rows.forEach {
            keys.addAll(it.keys)
        }
        columns = keys.toList()
    }

    fun addAll(newRows: List<Map<String, String>>) {
        val mutableRows = rows.toMutableList()
        mutableRows.addAll(newRows)
        rows = mutableRows.toList()
    }

    fun add(newRow: Map<String, String>) {
        val mutableRows = rows.toMutableList()
        mutableRows.add(newRow)
        rows = mutableRows.toList()
    }

    fun getAllColumnLengths(): Map<String, Int> {
        val a = "a"
        a.length
        return columns.associateWith { getColumnLength(it) }
    }

    fun getColumnLength(column: String): Int {
        if (column !in columns)
            throw IllegalArgumentException("No such column")
        return max(rows.maxOf {
            if (it[column] == null)
                4
            else
                it[column]!!.length
        }, column.length)
    }

    fun getCell(line: Int, column: String) = rows[line][column]
}

