package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.webapp.users.UsersController

/**
 * Spring Configuration for the Users Controller
 */
@Configuration
open class UsersConfig {
    /**
     * The Users Controller
     */
    @Bean
    open fun usersController() = UsersController()
}