package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.webapp.IdGenerator
import uk.co.grahamcox.worldbuilder.webapp.worlds.UsersController

/**
 * Spring Configuration for the Users controller
 */
@Configuration
open class UsersConfig {
    /**
     * Build the Users Controller
     * @return the controller
     */
    @Autowired
    @Bean
    open fun usersController(idGenerator: IdGenerator) = UsersController(idGenerator)
}