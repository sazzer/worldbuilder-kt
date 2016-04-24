package uk.co.grahamcox.worldbuilder.dao.embedded.transformers

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.time.*
import java.util.*

/**
 * Unit tests for the Date Time Transformer
 */
@RunWith(JUnitParamsRunner::class)
class DateTimeTransformerTest {
    /** The test subject */
    val testSubject = DateTimeTransformer()

    /**
     * Test getting the current time
     */
    @Test
    fun testNow() {
        val now = Instant.now()
        assertClose(now, "now")
    }

    /**
     * Test getting an exact time today
     */
    @Test
    @Parameters(method = "paramsForExactTime")
    fun testExactTimeToday(input: String, hour: Int, minute: Int, second: Int) {
        val expected = ZonedDateTime.now(ZoneId.of("UTC"))
                .withHour(hour)
                .withMinute(minute)
                .withSecond(second)
                .toInstant()
        assertClose(expected, input)
    }

    /**
     * Test offsetting the current time by a period
     */
    @Test
    @Parameters(method = "paramsForOffsetTime")
    fun testOffsetTime(input: String, expectedDuration: Duration) {
        val expected = Instant.now().plus(expectedDuration)
        assertClose(expected, input)
    }

    /**
     * Assert that the two values are to within the same second
     */
    fun assertClose(expected: Instant, input: String) {
        val transformed = testSubject.transform(input) as Date
        val difference = Duration.between(expected, transformed.toInstant())
        Assert.assertTrue("Difference in times was ${difference}", difference.abs().toMillis() < 1000)
    }

    /**
     * Test transforming an exact value of some kind
     */
    @Parameters(method = "paramsForExactValues")
    @Test
    fun testExactValues(input: Any?, expected: Any?) {
        Assert.assertEquals(expected, testSubject.transform(input))
    }

    /**
     * Parameters for the Exact Values tests
     */
    fun paramsForExactValues() = arrayOf(
            arrayOf<Any?>(null, null),
            arrayOf(1, 1),
            arrayOf(true, true),
            arrayOf(listOf<String>(), listOf<String>()),
            arrayOf("2016-04-24T05:23:16Z", Date.from(ZonedDateTime.of(2016, 4, 24, 5, 23, 16, 0, ZoneId.of("UTC")).toInstant())),
            arrayOf("2016-04-24T05:23:16+00:00", Date.from(ZonedDateTime.of(2016, 4, 24, 5, 23, 16, 0, ZoneId.of("UTC")).toInstant())),
            arrayOf("2016-04-24T05:23:16+04:00", Date.from(ZonedDateTime.of(2016, 4, 24, 1, 23, 16, 0, ZoneId.of("UTC")).toInstant()))
    )

    /**
     * Parameters for the Exact Time tests
     */
    fun paramsForExactTime() = arrayOf(
            arrayOf("now @ 07:00:00", 7, 0, 0),
            arrayOf("now @ 01:02:03", 1, 2, 3),
            arrayOf("now @ 23:59:59", 23, 59, 59),
            arrayOf("now @ 07:00", 7, 0, 0)
    )

    /**
     * Parameters for the Offset TIme tests
     */
    fun paramsForOffsetTime() = arrayOf(
            arrayOf("now + P1D", Duration.ofDays(1)),
            arrayOf("now + PT1H", Duration.ofHours(1)),
            arrayOf("now - P1D", Duration.ofDays(-1)),
            arrayOf("now - PT1H", Duration.ofHours(-1)),
            arrayOf("now + P1D + PT1H", Duration.ofDays(1).plusHours(1)),
            arrayOf("now + P1D - PT1H", Duration.ofDays(1).minusHours(1)),
            arrayOf("now - P1D + PT1H", Duration.ofDays(-1).plusHours(1))
    )
}