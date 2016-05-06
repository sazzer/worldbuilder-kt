package uk.co.grahamcox.worldbuilder.webapp.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.service.users.UserEditor
import uk.co.grahamcox.worldbuilder.service.users.UserFinder
import uk.co.grahamcox.worldbuilder.webapp.users.UserSchemaRegistration

/**
 * Spring Configuration for the API layer to work with Users records
 */
@Configuration
open class UsersConfig {
    /**
     * Configure the User parts of the Schema
     */
    @Autowired
    @Bean
    open fun userSchema(userFinder : UserFinder,
                        userEditor: UserEditor,
                        objectMapper: ObjectMapper) =
            UserSchemaRegistration(userFinder, userEditor, objectMapper)
}