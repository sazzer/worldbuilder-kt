package uk.co.grahamcox.worldbuilder.webapp.users

import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistrar
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistration

/**
 * GraphQL Schema Registration for the User details
 */
class UserSchemaRegistration(private val userByIdFetcher: UserByIdFetcher,
                             private val userCreator: UserCreator) : GraphQLRegistration {
    /**
     * Register all of the User portions of the schema
     * @param registrar The registrar to register the schema with
     */
    override fun register(registrar: GraphQLRegistrar) {
        registrar.newObject("user")
                .withDescription("Representation of a User")
                .apply {
                    withField("id")
                            .withDescription("The ID of the User")
                            .withType("id!")
                    withField("created")
                            .withDescription("When the User was created")
                            .withType("string!")
                    withField("updated")
                            .withDescription("When the User was updated")
                            .withType("string!")
                    withField("name")
                            .withDescription("The name of the User")
                            .withType("string!")
                    withField("email")
                            .withDescription("The email address of the User")
                            .withType("string")
                    withField("enabled")
                            .withDescription("If the User is enabled")
                            .withType("boolean!")
                    withField("verified")
                            .withDescription("If the User is verified")
                            .withType("boolean!")
                }

        registrar.newQuery("userById")
                .withDescription("Look up a User by ID")
                .withType("user")
                .withFetcher(userByIdFetcher)
                .withArgument("userId")
                .withDescription("The ID of the User to look up")
                .withType("id!")

        registrar.newInputObject("newUserInput")
                .withDescription("The details of the new user to create")
                .apply {
                    withField("clientMutationId")
                            .withDescription("The Client Mutation ID")
                            .withType("string!")
                    withField("name")
                            .withDescription("The name of the User")
                            .withType("string!")
                    withField("email")
                            .withDescription("The email address of the User")
                            .withType("string")
                }

        registrar.newObject("newUserResult")
                .withDescription("The result of creating a new user")
                .apply {
                    withField("clientMutationId")
                            .withDescription("The Client Mutation ID")
                            .withType("string!")
                    withField("user")
                            .withDescription("The user as it has been created")
                            .withType("user!")
                }

        registrar.newMutation("createUser")
                .withDescription("Register a new User")
                .withType("newUserResult!")
                .withFetcher(userCreator)
                .apply {
                    withArgument("input")
                            .withDescription("The details of the user to create")
                            .withType("newUserInput")
                }
    }
}