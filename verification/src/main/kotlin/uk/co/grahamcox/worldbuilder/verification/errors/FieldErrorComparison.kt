package uk.co.grahamcox.worldbuilder.verification.errors

import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.verification.DataTableEntry
import uk.co.grahamcox.worldbuilder.verification.FieldError
import uk.co.grahamcox.worldbuilder.verification.comparitor.Comparison

/**
 * Comparison to compare a Data Table Entry - where the key is the field name and the value is the error code - to a
 * field error to see if they are the same
 * @property fieldNameMap The map of field names to use
 */
class FieldErrorComparison(private val fieldNameMap: Map<String, String>) : Comparison<DataTableEntry, FieldError> {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(FieldErrorComparison::class.java)
    }

    /**
     * Compare the given data table entry to a field error.
     * The key of the data table entry must be an entry in the field names map, where the value in the field names map
     * is equal to the field in the Field Error.
     */
    override fun compare(expected: DataTableEntry, actual: FieldError): Boolean {
        val actualFieldName = fieldNameMap[expected.key]

        return if (actualFieldName == null) {
            LOG.warn("Unknown field name: {}", expected.key)
            false
        } else if (!actualFieldName.equals(actual.field)) {
            LOG.trace("Field name {} doesn't match actual value {}", actualFieldName, actual.field)
            false
        } else if (!expected.value.equals(actual.errorCode)) {
            LOG.trace("Error code {} doesn't match actual value {}", expected.value, actual.errorCode)
            false
        } else {
            true
        }
    }
}