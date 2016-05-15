package uk.co.grahamcox.worldbuilder.verification.comparitor

/**
 * Unit Tests for the Simple Field Comparator
 */
class SimpleFieldComparatorTest : FieldComparatorTestBase() {
    /** The test subject */
    override val testSubject = SimpleFieldComparator()

    /**
     * The parameters for the successful tests
     */
    override fun paramsForSuccess() = listOf(
            // Exact match
            arrayOf("Hello", "Hello"),
            // Booleans
            arrayOf("True", true),
            arrayOf("False", false)
    )

    /**
     * The parameters for the failure tests
     */
    override fun paramsForFailure() = listOf(
            // Exact match
            arrayOf("Hello", "World"),
            // Booleans
            arrayOf("True", false),
            arrayOf("False", true)
    )

    /**
     * The parameters for the error tests
     */
    override fun paramsForError() = listOf(
            // Unconvertable type
            arrayOf("[]", listOf<String>())
    )
}