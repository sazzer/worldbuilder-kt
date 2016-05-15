package uk.co.grahamcox.worldbuilder.verification.comparitor

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.time.*
import kotlin.system.exitProcess

/**
 * Unit tests for the Date Time Comparator
 */
@RunWith(JUnitParamsRunner::class)
abstract class FieldComparatorTestBase {
    /** The test subject */
    abstract val testSubject: FieldComparator

    /**
     * Test the cases where the comparison matches
     */
    @Test
    @Parameters(method = "paramsForSuccess")
    fun testSuccess(expected: String, actual: Any) {
        Assert.assertTrue(testSubject.compare(expected, actual))
    }

    /**
     * The parameters for the successful tests
     */
    abstract fun paramsForSuccess(): List<Array<out Any>>

    /**
     * Test the cases where the comparison fails
     */
    @Test
    @Parameters(method = "paramsForFailure")
    fun testFailure(expected: String, actual: Any) {
        Assert.assertFalse(testSubject.compare(expected, actual))
    }

    /**
     * The parameters for the failure tests
     */
    abstract fun paramsForFailure(): List<Array<out Any>>

    /**
     * Test the cases where the inputs are invalid
     */
    @Test(expected = IllegalArgumentException::class)
    @Parameters(method = "paramsForError")
    fun testError(expected: String, actual: Any) {
        testSubject.compare(expected, actual)
    }


    /**
     * The parameters for the error tests
     */
    abstract fun paramsForError(): List<Array<out Any>>
}