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

@RestController
@RequestMapping("/api/graphql")
open class GraphQLController {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(GraphQLController::class.java)
    }
    private val schema: GraphQLSchema

    init {
        val worldType = GraphQLObjectType.newObject()
                .name("world")
                .description("The details of a single world")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("id")
                        .description("The ID of the User")
                        .type(GraphQLNonNull(GraphQLString))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("name")
                        .description("The name of the User")
                        .type(GraphQLNonNull(GraphQLString))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("owner")
                        .description("The owner of this World")
                        .type(GraphQLNonNull(GraphQLTypeReference("user")))
                        .build())
                .build()

        val userLoginType = GraphQLObjectType.newObject()
                .name("login")
                .description("The details of a single login")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("provider")
                        .description("The login provider")
                        .type(GraphQLNonNull(GraphQLString))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("providerId")
                        .description("The ID as understood by the provider")
                        .type(GraphQLNonNull(GraphQLString))
                        .build())
                .build()

        val userType = GraphQLObjectType.newObject()
                .name("user")
                .description("A single user in the system")
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("id")
                        .description("The ID of the User")
                        .type(GraphQLNonNull(GraphQLString))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("name")
                        .description("The name of the User")
                        .type(GraphQLNonNull(GraphQLString))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("email")
                        .description("The Email Address of the User")
                        .type(GraphQLString)
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("banned")
                        .description("If the user is banned")
                        .type(GraphQLNonNull(GraphQLBoolean))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("verified")
                        .description("If the user is Verified")
                        .type(GraphQLNonNull(GraphQLBoolean))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("logins")
                        .description("The login provider details")
                        .type(GraphQLList(userLoginType))
                        .build())
                .field(GraphQLFieldDefinition.newFieldDefinition()
                        .name("worlds")
                        .description("The Worlds owned by this user")
                        .type(GraphQLList(worldType))
                        .dataFetcher { e -> listOf(
                                mapOf(
                                        "id" to "123456",
                                        "name" to "Example",
                                        "owner" to mapOf(
                                                "id" to "abcdef",
                                                "name" to "Graham",
                                                "email" to "graham@grahamcox.co.uk",
                                                "banned" to false,
                                                "verified" to true
                                        )
                                )
                        ) }
                        .build())
                .build()

        val queryType = GraphQLObjectType.newObject()
            .name("worldbuilderQuery")
            .field(GraphQLFieldDefinition.newFieldDefinition()
                    .type(GraphQLNonNull(GraphQLList(userType)))
                    .name("users")
                    .description("Filtered list of users")
                    .staticValue(listOf(
                            mapOf(
                                    "id" to "abcdef",
                                    "name" to "Graham",
                                    "email" to "graham@grahamcox.co.uk",
                                    "banned" to false,
                                    "verified" to true,
                                    "logins" to listOf(
                                            mapOf(
                                                    "provider" to "local",
                                                    "providerId" to "graham@grahamcox.co.uk"
                                            ),
                                            mapOf(
                                                    "provider" to "twitter",
                                                    "providerId" to "@grahamcox82"
                                            )
                                    )
                            )
                    ))
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