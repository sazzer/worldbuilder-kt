package uk.co.grahamcox.dao.embedded.mongodb.transformers

import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Duration
import java.time.ZonedDateTime
import java.util.*

/**
 * Transformer to transform a String value into a Date/Time value
 */
class DateTimeTransformer : Transformer {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(DateTimeTransformer::class.java)

        /** Regex to match a Period Expression */
        private val PERIOD_REGEX = "P(?:T\\d+[HMS]|\\d+[YMD])+"

        /** Regex to match a Period Offset */
        private val PERIOD_OFFSET_REGEX = "(?:\\s*(?<plusMinus>[+-])\\s*(?<period>${DateTimeTransformer.Companion.PERIOD_REGEX}))"

        /** Regex to match a number of periods */
        private val PERIODS_REGEX = "(?<periods>${DateTimeTransformer.Companion.PERIOD_OFFSET_REGEX}*)"

        /** Regex to match a time value */
        private val TIME_REGEX = "(?<hour>\\d\\d)\\:(?<minute>\\d\\d)(?:\\:(?<second>\\d\\d))?"

        /** Regex to match an exact time setting */
        private val EXACT_TIME_REGEX = "(?:\\s*@\\s*${DateTimeTransformer.Companion.TIME_REGEX})?"

        /** Regex to match a relative time string */
        private val RELATIVE_REGEX = "^now${DateTimeTransformer.Companion.EXACT_TIME_REGEX}${DateTimeTransformer.Companion.PERIODS_REGEX}$".toPattern()

        /** The clock to use */
        private val CLOCK = Clock.systemUTC()
    }

    /**
     * Transform the input value.
     * If it's a String that looks like it's relative to now then it will be processed as such.
     * Otherwise if it's just a String it will be treated as an absolute value in ISO-8601 format
     * Otherwise it will be returned as-is
     * @param input The input value to transform
     * @return the transformed value
     */
    override fun transform(input: Any?) = when(input) {
        null -> null
        is String -> {
            val match = DateTimeTransformer.Companion.RELATIVE_REGEX.matcher(input)
            val processedTime = if (match.matches()) {
                DateTimeTransformer.Companion.LOG.debug("Input string {} is being processed as a relative time string: {}", input, match)
                val now = ZonedDateTime.now(DateTimeTransformer.Companion.CLOCK)

                val hourValue = match.group("hour")
                val minuteValue = match.group("minute")
                val secondValue = match.group("second")?:"00"

                val offset = if (hourValue != null) {
                    DateTimeTransformer.Companion.LOG.debug("Setting exact time {}:{}:{}", hourValue, minuteValue, secondValue)
                    now
                        .withHour(Integer.parseInt(hourValue))
                        .withMinute(Integer.parseInt(minuteValue))
                        .withSecond(Integer.parseInt(secondValue))
                } else {
                    now
                }

                val periodsValue = match.group("periods")

                val result = if (periodsValue != null) {
                    DateTimeTransformer.Companion.LOG.debug("Adjusting time by periods {}", periodsValue)
                    val combinedPeriod = buildCombinedPeriod(periodsValue)

                    offset.plus(combinedPeriod)
                } else {
                    offset
                }

                result
            } else {
                DateTimeTransformer.Companion.LOG.debug("Input string {} is being processed as an absolute time string", input)
                ZonedDateTime.parse(input)
            }

            DateTimeTransformer.Companion.LOG.debug("Relative time from {} is {}", input, processedTime)
            Date.from(processedTime.toInstant())
        }
        else -> input
    }

    /**
     * Build a single combined Period object from the given string
     * @param periodsValue The input period string to parse
     * @return the combined period
     */
    fun buildCombinedPeriod(periodsValue: String): Duration {
        val periodMatch = DateTimeTransformer.Companion.PERIOD_OFFSET_REGEX.toPattern().matcher(periodsValue)
        var result = Duration.ZERO

        while (periodMatch.find()) {
            val plusMinus = periodMatch.group("plusMinus")
            val periodString = periodMatch.group("period")
            DateTimeTransformer.Companion.LOG.debug("Performing {} {} {}", result, plusMinus, periodString)

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