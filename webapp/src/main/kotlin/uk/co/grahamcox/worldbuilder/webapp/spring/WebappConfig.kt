package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import uk.co.grahamcox.worldbuilder.webapp.DebugController
import java.time.Clock

/**
 * Spring configuration for the webapp
 */
@Configuration
@Import(
        WebMvcConfig::class
)
open class WebappConfig {
    /**
     * Build the Debug Controller
     * @param clock The clock to use
     * @return the debug controller
     */
    @Autowired
    @Bean
    open fun debugController(clock: Clock) = DebugController(clock)
}