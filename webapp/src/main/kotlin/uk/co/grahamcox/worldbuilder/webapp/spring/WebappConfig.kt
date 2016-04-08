package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Spring configuration for the webapp
 */
@Configuration
@Import(
        WebMvcConfig::class
)
open class WebappConfig {
}