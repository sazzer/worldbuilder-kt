package uk.co.grahamcox.worldbuilder.webapp.spring

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import uk.co.grahamcox.worldbuilder.webapp.DebugController
import uk.co.grahamcox.worldbuilder.webapp.ErrorSchemaRegistration
import uk.co.grahamcox.worldbuilder.webapp.GraphQLController
import uk.co.grahamcox.worldbuilder.webapp.IdGenerator
import java.time.Clock

/**
 * Spring configuration for the webapp
 */
@Configuration
@Import(
        WebMvcConfig::class,
        UsersConfig::class,
        GraphQLConfig::class
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

    /**
     * The ID Generator to use
     * @param objectMapper The JSON Object Mapper
     * @return the ID Generator
     */
    @Autowired
    @Bean
    open fun idGenerator(objectMapper: ObjectMapper) = IdGenerator(objectMapper)

    /**
     * Register the parts of the GraphQL Schema that handles error responses
     */
    @Bean
    open fun errorSchema() = ErrorSchemaRegistration()
}