package uk.co.grahamcox.worldbuilder.webapp

import com.fasterxml.jackson.annotation.JsonCreator
import graphql.GraphQL
import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.*
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistrar
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLSchemaBuilder

@RestController
@RequestMapping("/api/graphql")
open class GraphQLController {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(GraphQLController::class.java)
    }
    private val schema: GraphQLSchema

    init {
        val registry = GraphQLRegistrar()

        registry.newObject("user")
                .withDescription("Representation of a user")
                .apply {
                    withField("id")
                            .withDescription("The ID of the User")
                            .withType("id!")
                    withField("name")
                            .withDescription("The name of the User")
                            .withType("string!")
                    withField("email")
                            .withDescription("The email address of the User")
                            .withType("string")
                }

        registry.newQuery("user")
                .withDescription("Get a single user by ID")
                .withType("user")
                .apply {
                    withArgument("id")
                            .withDescription("The ID of the user to fetch")
                            .withType("id!")
                    withFetcher(DataFetcher { e -> mapOf(
                            "id" to e.arguments["id"],
                            "name" to "Graham",
                            "email" to "graham@grahamcox.co.uk"
                    )})
                }

        registry.newQuery("version")
                .withDescription("The current API Version")
                .withType("string!")
                .withValue("1.0")

        schema = GraphQLSchemaBuilder(registry).buildGraphQLSchema()
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
            mapOf("errors" to result.errors)
        } else {
            mapOf("data" to result.data)
        }
    }
}

class GraphQLJsonInput {
    var query: String = ""
    var variables: Any? = null
    var operationName: String? = null
    override fun toString(): String{
        return "GraphQLJsonInput(query='$query', variables=$variables, operationName=$operationName)"
    }
}