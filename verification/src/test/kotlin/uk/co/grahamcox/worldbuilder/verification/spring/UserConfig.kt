package uk.co.grahamcox.worldbuilder.verification.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.verification.populator.ModelPopulator
import uk.co.grahamcox.worldbuilder.verification.graphql.GraphQLClient
import uk.co.grahamcox.worldbuilder.verification.users.UserCreator
import uk.co.grahamcox.worldbuilder.verification.users.UserFacade

/**
 * Spring config for working with users
 */
@Configuration
open class UserConfig {
    /**
     * Populator to populate new user objects
     */
    @Bean
    open fun newUserPopulator() = ModelPopulator(mapOf(
            "Name" to "name",
            "Email" to "email"
    ))

    /**
     * Mechanism for creating users
     */
    @Autowired
    @Bean
    open fun userCreator(graphQLClient: GraphQLClient, objectMapper: ObjectMapper) =
            UserCreator(graphQLClient, objectMapper)

    /**
     * Facade for working with users
     */
    @Autowired
    @Bean
    open fun userFacade(userCreator: UserCreator) = UserFacade(userCreator)
}