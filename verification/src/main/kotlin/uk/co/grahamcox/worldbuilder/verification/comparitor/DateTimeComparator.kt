package uk.co.grahamcox.worldbuilder.verification.comparitor

import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime

/**
 * Implementation of the FieldComparator that will compare date/time classes
 */
class DateTimeComparator(private val clock: Clock) : FieldComparator {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(DateTimeComparator::class.java)

        /** Regex to match a Period Expression */
        private val PERIOD_REGEX = "P(?:T\\d+[HMS]|\\d+[YMD])+"

        /** Regex to match a Period Offset */
        private val PERIOD_OFFSET_REGEX = "(?:\\s*(?<plusMinus>[+-])\\s*(?<period>${PERIOD_REGEX}))"

        /** Regex to match a number of periods */
        private val PERIODS_REGEX = "(?<periods>${PERIOD_OFFSET_REGEX}*)"

        /** Regex to match a time value */
        private val TIME_REGEX = "(?<hour>\\d\\d)\\:(?<minute>\\d\\d)(?:\\:(?<second>\\d\\d))?"

        /** Regex to match an exact time setting */
        private val EXACT_TIME_REGEX = "(?:\\s*@\\s*${TIME_REGEX})?"

        /** Regex to match a relative time string */
        private val RELATIVE_REGEX = "now${EXACT_TIME_REGEX}${PERIODS_REGEX}"
        
        /** Regex to match an absolute time */
        private val ABSOLUTE_TIME_REGEX = "^${RELATIVE_REGEX}$".toRegex()

        /** Regex to match an upper limit on the time */
        private val BEFORE_TIME_REGEX = "^before\\s+(?<time>${RELATIVE_REGEX})$".toRegex()

        /** Regex to match a lower limit on the time */
        private val AFTER_TIME_REGEX = "^after\\s+(?<time>${RELATIVE_REGEX})$".toRegex()
        
        /** Regex to match between two times */
        private val BETWEEN_TIMES_REGEX = 
                "^between\\s+(?<firstTime>${RELATIVE_REGEX})\\s+and\\s+(?<secondTime>${RELATIVE_REGEX})$".toRegex()
        
        /** Regex to match a period offset of a time */
        private val WITHIN_PERIOD_REGEX = "^(?<time>${RELATIVE_REGEX})\\s+within\\s(?<within>${PERIOD_REGEX})$".toRegex()
    }
    
    /**
     * Compare the expected string to the actual Date/Time value.
     * The Date/Time value must be an Instant or something that can be converted to an Instant.
     * The Expected value must be one of a set of possible input strings. Either:
     * * An absolute time
     * * "before " an absolute time
     * * "after " an absolute time
     * * "between " an absolute time " and " another absolute time
     * * An absolute time " within " A period
     */
    override fun compare(expected: String, value: Any): Boolean {
        val instant = when (value) {
            is Instant -> value
            else -> throw IllegalArgumentException("Unable to compare to an object of type ${value.javaClass}")
        }
        
        val (lower, upper) = if (ABSOLUTE_TIME_REGEX.matches(expected)) {
            val absoluteTime = parseAbsoluteTime(expected)
            Pair(absoluteTime, absoluteTime)
        } else if (BEFORE_TIME_REGEX.matches(expected)) {
            val timeString = BEFORE_TIME_REGEX.toPattern().matcher(expected).group("time")
            val absoluteTime = parseAbsoluteTime(timeString)
            Pair(null, absoluteTime)
        } else if (AFTER_TIME_REGEX.matches(expected)) {
            val timeString = AFTER_TIME_REGEX.toPattern().matcher(expected).group("time")
            val absoluteTime = parseAbsoluteTime(timeString)
            Pair(absoluteTime, null)
        } else if (BETWEEN_TIMES_REGEX.matches(expected)) {
            val matcher = BETWEEN_TIMES_REGEX.toPattern().matcher(expected)
            val firstTime = parseAbsoluteTime(matcher.group("firstTime"))
            val secondTime = parseAbsoluteTime(matcher.group("secondTime"))
            Pair(firstTime, secondTime)
        } else if (WITHIN_PERIOD_REGEX.matches(expected)) {
            val matcher = WITHIN_PERIOD_REGEX.toPattern().matcher(expected)
            val time = parseAbsoluteTime(matcher.group("time"))
            val within = matcher.group("within")
            val duration = Duration.parse(within)

            Pair(time.minus(duration), time.plus(duration))
        } else {
            throw IllegalArgumentException("Expected string doesn't match any valid inputs")
        }

        LOG.debug("Comparing actual time {} to lower bound {} and upper bound {}", instant, lower, upper)

        return if (lower != null && lower.isBefore(instant)) {
            LOG.debug("Lower bound {} is before actual time {}", lower, instant)
            false
        } else if (upper != null && upper.isAfter(instant)) {
            LOG.debug("Upper bound {} is after actual time {}", upper, instant)
            false
        } else {
            true
        }
    }

    /**
     * Parse a provided string to produce an absolute time
     * @param input The input string to parse
     * @return the absolute time it represents
     */
    private fun parseAbsoluteTime(input: String): Instant {
        val match = RELATIVE_REGEX.toPattern().matcher(input)
        val processedTime = if (match.matches()) {
            LOG.debug("Input string {} is being processed as a relative time string: {}", input, match)
            val now = ZonedDateTime.now(clock)

            val hourValue = match.group("hour")
            val minuteValue = match.group("minute")
            val secondValue = match.group("second")?:"00"

            val offset = if (hourValue != null) {
                LOG.debug("Setting exact time {}:{}:{}", hourValue, minuteValue, secondValue)
                now
                        .withHour(Integer.parseInt(hourValue))
                        .withMinute(Integer.parseInt(minuteValue))
                        .withSecond(Integer.parseInt(secondValue))
            } else {
                now
            }

            val periodsValue = match.group("periods")

            val result = if (periodsValue != null) {
                LOG.debug("Adjusting time by periods {}", periodsValue)
                val combinedPeriod = buildCombinedPeriod(periodsValue)

                offset.plus(combinedPeriod)
            } else {
                offset
            }

            result
        } else {
            LOG.debug("Input string {} is being processed as an absolute time string", input)
            ZonedDateTime.parse(input)
        }

        return processedTime.toInstant()
    }
    
    /**
     * Build a single combined Period object from the given string
     * @param periodsValue The input period string to parse
     * @return the combined period
     */
   private fun buildCombinedPeriod(periodsValue: String): Duration {
        val periodMatch = PERIOD_OFFSET_REGEX.toPattern().matcher(periodsValue)
        var result = Duration.ZERO

        while (periodMatch.find()) {
            val plusMinus = periodMatch.group("plusMinus")
            val periodString = periodMatch.group("period")
            LOG.debug("Performing {} {} {}", result, plusMinus, periodString)

            val period = Duration.parse(periodString)
            result = when (plusMinus) {
                "+" -> result.plus(period)
                "-" -> result.minus(period)
                else -> throw IllegalStateException("Somehow got a non +/- operator: ${plusMinus}")
            }
        }

        return result
    }
}