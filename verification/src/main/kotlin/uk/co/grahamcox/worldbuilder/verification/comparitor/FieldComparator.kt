package uk.co.grahamcox.worldbuilder.verification.comparitor

/**
 * Mechanism by which we can compare a model field to the expected value
 */
interface FieldComparator {
    /**
     * Compare the expected value - as a string - to the model field and see if they match
     * @param expected The expected value
     * @param value The model field value
     * @return true if the two represent the same value. False if not
     */
    fun compare(expected: String, value: Any) : Boolean
}