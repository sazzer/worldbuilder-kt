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
class DateTimeComparatorTest {
    /** The clock to use */
    private val clock = Clock.fixed(ZonedDateTime.of(2016, 5, 15, 10, 5, 0, 0, ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"))

    /** The test subject */
    private val testSubject = DateTimeComparator(clock)

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
    fun paramsForSuccess() = listOf(
            // Absolute match
            arrayOf(clock.instant().toString(), clock.instant()),
            // Relative matches
            arrayOf("now", clock.instant()),
            arrayOf("now + PT1H", clock.instant().plus(Duration.ofHours(1))),
            // Lower bound on time
            arrayOf("after now", clock.instant().plus(Duration.ofSeconds(1))),
            // Upper bound on time
            arrayOf("before now", clock.instant().minus(Duration.ofSeconds(1))),
            // Time within duration
            arrayOf("now within PT5S", clock.instant()),
            arrayOf("now within PT5S", clock.instant().plus(Duration.ofSeconds(3))),
            arrayOf("now within PT5S", clock.instant().minus(Duration.ofSeconds(3))),
            arrayOf("${clock.instant().toString()} within PT5S", clock.instant().minus(Duration.ofSeconds(3))),
            // Within two points of time
            arrayOf("between now and now + PT5S", clock.instant()),
            arrayOf("between now and now + PT5S", clock.instant().plus(Duration.ofSeconds(3))),
            arrayOf("between ${clock.instant().toString()} and ${clock.instant().plus(Duration.ofSeconds(5)).toString()}",
                    clock.instant().plus(Duration.ofSeconds(3)))
    )

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
    fun paramsForFailure() = listOf(
            // Absolute match
            arrayOf(clock.instant().plus(Duration.ofSeconds(1)).toString(), clock.instant()),
            // Relative matches
            arrayOf("now", clock.instant().plus(Duration.ofSeconds(1))),
            arrayOf("now + PT1H", clock.instant()),
            // Lower bound on time
            arrayOf("after now", clock.instant().minus(Duration.ofSeconds(1))),
            // Upper bound on time
            arrayOf("before now", clock.instant().plus(Duration.ofSeconds(1))),
            // Time within duration
            arrayOf("now within PT5S", clock.instant().plus(Duration.ofSeconds(6))),
            arrayOf("now within PT5S", clock.instant().minus(Duration.ofSeconds(6))),
            arrayOf("${clock.instant().toString()} within PT5S", clock.instant().minus(Duration.ofSeconds(6))),
            // Within two points of time
            arrayOf("between now and now + PT5S", clock.instant().minus(Duration.ofSeconds(1))),
            arrayOf("between now and now + PT5S", clock.instant().plus(Duration.ofSeconds(6))),
            arrayOf("between ${clock.instant().toString()} and ${clock.instant().plus(Duration.ofSeconds(5)).toString()}",
                    clock.instant().plus(Duration.ofSeconds(6)))
    )

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
    fun paramsForError() = listOf(
            arrayOf("invalid time", clock.instant()),
            arrayOf("now", "now")
    )
}