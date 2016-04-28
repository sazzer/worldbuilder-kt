package uk.co.grahamcox.worldbuilder.webapp

import com.fasterxml.jackson.annotation.JsonCreator
import graphql.GraphQL
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/graphql")
open class GraphQLController {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(GraphQLController::class.java)
    }
    private val schema: GraphQLSchema

    init {
        val queryType = GraphQLObjectType.newObject()
            .name("helloWorldQuery")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                .type(GraphQLString)
                .name("hello")
                .staticValue("world")
                .build())
            .build()

        schema = GraphQLSchema.newSchema()
            .query(queryType)
            .build()
    }

    /**
     * Actually handle a GraphQL Request
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST),
            consumes = arrayOf("application/graphql"),
            produces = arrayOf("application/json"))
    open fun graphql(@RequestBody query: String): Any {
        val result = GraphQL(schema).execute(query)
        LOG.debug("Processing query: {} with data {} and errors {}", query, result.data, result.errors)
        return if (result.errors.isNotEmpty()) {
            result.errors
        } else {
            result.data
        }
    }

    /**
     * Actually handle a GraphQL Request
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST),
            consumes = arrayOf("application/json"),
            produces = arrayOf("application/json"))
    open fun graphqlAsJson(@RequestBody query: GraphQLJsonInput): Any {
        val result = GraphQL(schema).execute(query.query)
        LOG.debug("Processing query: {} with data {} and errors {}", query, result.data, result.errors)
        return if (result.errors.isNotEmpty()) {
            result.errors
        } else {
            result.data
        }
    }
}

class GraphQLJsonInput {
    var query: String = ""
    var variables: Any? = null
}