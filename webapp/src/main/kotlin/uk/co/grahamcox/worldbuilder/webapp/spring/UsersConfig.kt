package uk.co.grahamcox.worldbuilder.webapp.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.service.users.UserEditor
import uk.co.grahamcox.worldbuilder.service.users.UserFinder
import uk.co.grahamcox.worldbuilder.webapp.MutationFetcher
import uk.co.grahamcox.worldbuilder.webapp.users.*

/**
 * Spring Configuration for the API layer to work with Users records
 */
@Configuration
open class UsersConfig {
    /**
     * The Data Fetcher for Users by ID
     */
    @Autowired
    @Bean
    open fun userByIdFetcher(userFinder : UserFinder) = UserByIdFetcher(userFinder)

    /**
     * The Data Fetcher for creating users
     */
    @Autowired
    @Bean
    open fun userCreator(userEditor: UserEditor, objectMapper: ObjectMapper) =
            MutationFetcher(UserCreator(userEditor),
                    UserInput::class.java,
                    "user",
                    objectMapper)

    /**
     * Configure the User parts of the Schema
     */
    @Autowired
    @Bean
    open fun userSchema(userByIdFetcher: UserByIdFetcher,
                        userCreator: MutationFetcher<UserInput, UserModel>) =
            UserSchemaRegistration(userByIdFetcher, userCreator)
}