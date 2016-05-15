package uk.co.grahamcox.worldbuilder.verification.comparitor

import java.time.Clock
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Unit tests for the Date Time Comparator
 */
class DateTimeComparatorTest : FieldComparatorTestBase() {
    /** The clock to use */
    private val clock = Clock.fixed(ZonedDateTime.of(2016, 5, 15, 10, 5, 0, 0, ZoneId.of("UTC")).toInstant(), ZoneId.of("UTC"))

    /** The test subject */
    override val testSubject = DateTimeComparator(clock)

    /**
     * The parameters for the successful tests
     */
    override fun paramsForSuccess() = listOf(
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
     * The parameters for the failure tests
     */
    override fun paramsForFailure() = listOf(
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
     * The parameters for the error tests
     */
    override fun paramsForError() = listOf(
            arrayOf("invalid time", clock.instant()),
            arrayOf("now", "now")
    )
}