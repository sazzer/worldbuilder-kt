package uk.co.grahamcox.worldbuilder.verification.comparitor

import org.slf4j.LoggerFactory

/**
 * Result of comparing two collections
 * @property matched The matched entries
 * @property missedExpected The expected entries that weren't present in the actual entries
 * @property missedActual The actual entries that weren't present in the expected entries
 */
data class CollectionComparatorResult<Expected, Actual>(
        val matched: Collection<Pair<Expected, Actual>>,
        val missedExpected: Collection<Expected>,
        val missedActual: Collection<Actual>
) {
    /** Whether we have an exact match - i.e. there are no missed matches on either side */
    val exactMatch = (missedActual.isEmpty() && missedExpected.isEmpty())
    /** Whether all of the expected entries were present, even if other ones were present too */
    val allExpectedMatch = missedExpected.isEmpty()
}

/**
 * Comparison for comparing an expected value to an actual one
 */
interface Comparison<Expected, Actual> {
    /**
     * Compare the two values to see if they represent the same thing
     * @param expected The expected value
     * @param actual The actual value
     * @return true if they represent the same thing
     */
    fun compare(expected: Expected, actual: Actual): Boolean
}

/**
 * Comparator to compare two collections to see what the matches are
 * @property comparison The comparison method to use
 */
class CollectionComparator<Expected, Actual>(private val comparison: Comparison<Expected, Actual>) {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(CollectionComparator::class.java)
    }
    /**
     * Compare the list of expected results to the list of actual results to see which ones match
     * @param expected The expected results
     * @param actual The actual results
     * @return the match details
     */
    fun compare(expected: Collection<Expected>, actual: Collection<Actual>) : CollectionComparatorResult<Expected, Actual> {
        val matched = mutableListOf<Pair<Expected, Actual>>()
        val missedExpected = mutableListOf<Expected>()
        val missedActual = mutableListOf<Actual>()

        missedExpected.addAll(expected)
        missedActual.addAll(actual)

        val expectedIterator = missedExpected.iterator()
        while (expectedIterator.hasNext()) {
            val nextExpected = expectedIterator.next()

            val actualIterator = missedActual.iterator()
            while (actualIterator.hasNext()) {
                val nextActual = actualIterator.next()
                val matches = comparison.compare(nextExpected, nextActual)
                LOG.trace("Matching expected value {} to actual value {} with result {}",
                        nextExpected, nextActual, matches)

                if (matches) {
                    matched.add(Pair(nextExpected, nextActual))
                    expectedIterator.remove()
                    actualIterator.remove()
                    break
                }
            }
        }

        val result = CollectionComparatorResult(matched,
                missedExpected,
                missedActual)

        LOG.debug("Comparing expected values {} to actual values {}: {}", expected, actual, result)
        return result
    }
}