package uk.co.grahamcox.worldbuilder.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

/**
 * Controller that accepts GraphQL requests
 * @property schema The GraphQL Schema to use
 * @property objectMapper The Object Mapper to decode incoming variables with
 */
@RestController
@RequestMapping("/api/graphql")
open class GraphQLController(private val schema: GraphQLSchema,
                             private val objectMapper: ObjectMapper) {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(GraphQLController::class.java)
    }

    /**
     * Actually handle a GraphQL Request
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST),
            consumes = arrayOf("application/graphql"),
            produces = arrayOf("application/json"))
    open fun graphql(@RequestBody query: String): Any {
        val input = GraphQLJsonInput(query = query)
        return graphqlAsJson(input)
    }

    /**
     * Actually handle a GraphQL Request
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST),
            consumes = arrayOf("application/json"),
            produces = arrayOf("application/json"))
    open fun graphqlAsJson(@RequestBody query: GraphQLJsonInput): Any {
        val variables: Map<String, Any?> = if (query.variables == null) {
            mapOf<String, Any?>()
        } else {
            objectMapper.readValue(query.variables, Map::class.java) as Map<String, Any?>
        }

        val result = GraphQL(schema).execute(query.query, query.operationName, null, variables)
        LOG.debug("Processing query: {} with data {} and errors {}", query, result.data, result.errors)
        return mapOf(
                "errors" to result.errors,
                "data" to result.data
        )
    }
}

/**
 * Representation of the GraphQL Input when received as a JSON Payload
 * @property query The actual query
 * @property variables JSON String representing the variables to use, if any
 * @property operationName The name of the operation to perform
 */
data class GraphQLJsonInput(var query: String = "",
                            var variables: String? = null,
                            var operationName: String? = null)
