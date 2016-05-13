package uk.co.grahamcox.worldbuilder.verification.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.verification.ModelPopulator
import uk.co.grahamcox.worldbuilder.verification.graphql.GraphQLClient
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
    open fun newUserPopulator() = ModelPopulator()

    /**
     * Facade for working with users
     */
    @Autowired
    @Bean
    open fun userFacade(graphQLClient: GraphQLClient, objectMapper: ObjectMapper) =
            UserFacade(graphQLClient, objectMapper)
}