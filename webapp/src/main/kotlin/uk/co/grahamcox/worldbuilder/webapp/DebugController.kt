package uk.co.grahamcox.worldbuilder.webapp

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.Swagger
import java.time.Clock
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Controller for getting some debug information out of the server
 */
@Controller
@RequestMapping("/api/debug")
open class DebugController(private val clock: Clock) {
    /**
     * Get the current server time
     * @param timezone The timezone to return the time in. If not specified then uses UTC
     * @return the current server time
     */
    @RequestMapping("/now")
    @ResponseBody
    @Swagger(value = "Return the server time", tags = arrayOf("debug"))
    fun now(@RequestParam(name = "tz", required = false, defaultValue = "UTC") timezone: String) =
            ZonedDateTime.now(clock).withZoneSameInstant(ZoneId.of(timezone))
}