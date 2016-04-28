package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.service.users.UserFinder
import uk.co.grahamcox.worldbuilder.webapp.IdGenerator
import uk.co.grahamcox.worldbuilder.webapp.users.UsersController

/**
 * Spring Configuration for the Users Controller
 */
@Configuration
open class UsersConfig {
    /**
     * The Users Controller
     */
    @Autowired
    @Bean
    open fun usersController(userFinder: UserFinder, idGenerator: IdGenerator) = UsersController(userFinder, idGenerator)
}