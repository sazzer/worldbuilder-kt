package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.service.worlds.WorldFinder
import uk.co.grahamcox.worldbuilder.webapp.IdGenerator
import uk.co.grahamcox.worldbuilder.webapp.worlds.WorldsController

/**
 * Spring Configuration for the Worlds controller
 */
@Configuration
open class WorldsConfig {
    /**
     * Build the Worlds Controller
     * @return the controller
     */
    @Autowired
    @Bean
    open fun worldsController(idGenerator: IdGenerator) = WorldsController(WorldFinder(), idGenerator)
}