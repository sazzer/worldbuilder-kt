package uk.co.grahamcox.worldbuilder.verification.comparitor

import org.apache.commons.jxpath.JXPathContext
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.verification.DataTableEntry

/**
 * Details of a missed match on a comparison
 * @property key The key of the missed match
 * @property expected The expected value
 * @property actual The actual value
 */
data class MissedMatch(val key: String, val expected: String, val actual: Any?)

/**
 * Definition of a path into the model to compare
 * @property path The actual JXPath
 * @property comparator The comparator to use
 */
data class FieldPath(val path: String, val comparator: FieldComparator = SimpleFieldComparator())

/**
 * Means to compare a single model object to a list of expected values
 * @property paths Map of the JXPaths to use for reading from the object
 */
class SingleModelComparator(private val paths: Map<String, FieldPath>) {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(SingleModelComparator::class.java)
    }

    /**
     * Compare the expected fields to the model object to see what the matches are
     * @param expected The expected fields
     * @param actual The actual object to compare with
     * @return the details of any matches that we didn't make
     */
    fun compare(expected: Collection<DataTableEntry>, actual: Any): List<MissedMatch> {
        val jxpath = JXPathContext.newContext(actual)

        val validEntries = expected.filter {
            if (!paths.containsKey(it.key)) {
                LOG.warn("Unknown field {} for target type {}", it.key, actual)
            }
            paths.containsKey(it.key)
        }

        val missedEntries = validEntries.map {
            val path = paths[it.key]!!
            val value = jxpath.getValue(path.path)
            val matches = path.comparator.compare(it.value, value)

            if (matches) {
                null
            } else {
                MissedMatch(it.key, it.value, value)
            }
        }.filterNotNull()

        LOG.info("Missed matches: {}", missedEntries)
        return missedEntries
    }
}