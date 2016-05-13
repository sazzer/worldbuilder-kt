package uk.co.grahamcox.worldbuilder.verification

import cucumber.api.DataTable

/**
 * Representation of a single entry in a data table
 * @property key The key of the entry
 * @property value The value of the entry
 */
data class DataTableEntry(val key: String, val value: String)

/**
 * Helper functions to work with Data Tables
 */
object DataTableParser {
    /**
     * Parse a Tall data table. This is a data table where the keys are in the first column, and the values are in
     * all the subsequent columns
     * @param dataTable The data table to parse
     * @return the parsed data table
     */
    fun parseTall(dataTable: DataTable) = parseWide(dataTable.transpose())

    /**
     * Parse a Wide data table. This is a data table where the keys are in the first row, and the values are in
     * all the subsequent rows
     * @param dataTable The data table to parse
     * @return the parsed data table
     */
    fun parseWide(dataTable: DataTable) : List<List<DataTableEntry>> {
        val rows = dataTable.raw()

        return if (rows.size == 0) {
            throw IllegalArgumentException("Empty data table provided")
        } else if (rows.size == 1) {
            throw IllegalArgumentException("No values in the data table")
        } else {
            val headings = rows[0]
            val dataRows = rows.drop(1)
            dataRows.map { row -> row }
                    .map { row -> headings.zip(row) }
                    .map { row ->
                        row.map { DataTableEntry(it.first, it.second) }
                    }
        }
    }

    /**
     * Parse a Wide data table that has exactly one row of data.
     * @param dataTable The data table to parse
     * @return the parsed data table
     */
    fun parseSingleWide(dataTable: DataTable) : List<DataTableEntry> {
        val parsed = parseWide(dataTable);
        if (parsed.size > 1) {
            throw IllegalArgumentException("Too many sets of data parsed")
        }
        return parsed[0]
    }

    /**
     * Parse a Tall data table that has exactly one of data.
     * @param dataTable The data table to parse
     * @return the parsed data table
     */
    fun parseSingleTall(dataTable: DataTable) = parseSingleWide(dataTable.transpose())
}