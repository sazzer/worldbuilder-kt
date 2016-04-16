package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.webapp.swagger.SwaggerController
import uk.co.grahamcox.worldbuilder.webapp.swagger.SwaggerSchemaBuilder
import uk.co.grahamcox.worldbuilder.webapp.swagger.model.Schema

/**
 * Configuration for the Swagger Documentation
 */
@Configuration
open class SwaggerConfig {
    /**
     * The Swagger Schema
     */
    @Bean
    open fun swaggerSchema() = SwaggerSchemaBuilder()


    /**
     * Build the Swagger Controller
     * @return the swagger controller
     */
    @Bean
    open fun swaggerController() = SwaggerController()
}