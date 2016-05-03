package uk.co.grahamcox.worldbuilder.webapp.spring

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.webapp.GraphQLController
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistrar
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistration
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLSchemaBuilder

/**
 * Spring Configuration for the GraphQL setup
 */
@Configuration
open class GraphQLConfig {
    /**
     * Build the GraphQL Schema
     */
    @Autowired
    @Bean
    open fun graphqlSchema(applicationContext: ApplicationContext): GraphQLSchema {
        val registrationDetails = applicationContext.getBeansOfType(GraphQLRegistration::class.java)

        val registrar = GraphQLRegistrar()
        registrationDetails.values.forEach { it.register(registrar) }

        return GraphQLSchemaBuilder(registrar).buildGraphQLSchema()
    }

    /**
     * Build the GraphQL Controller
     */
    @Autowired
    @Bean
    open fun graphqlController(schema: GraphQLSchema, objectMapper: ObjectMapper) = GraphQLController(schema, objectMapper)
}