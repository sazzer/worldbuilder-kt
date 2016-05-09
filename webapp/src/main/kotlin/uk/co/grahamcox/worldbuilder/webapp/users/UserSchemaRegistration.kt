package uk.co.grahamcox.worldbuilder.webapp.users

import com.fasterxml.jackson.databind.ObjectMapper
import uk.co.grahamcox.worldbuilder.service.users.UserEditor
import uk.co.grahamcox.worldbuilder.service.users.UserFinder
import uk.co.grahamcox.worldbuilder.webapp.MutationFetcher
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistrar
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistration

/**
 * GraphQL Schema Registration for the User details
 */
class UserSchemaRegistration(private val userFinder: UserFinder,
                             private val userEditor: UserEditor,
                             private val objectMapper: ObjectMapper) : GraphQLRegistration {
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
                .withFetcher(UserByIdFetcher(userFinder))
                .withArgument("userId")
                .withDescription("The ID of the User to look up")
                .withType("id!")

        registrar.newInputObject("newUserInput")
                .withDescription("The details of the new user to create")
                .apply {
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
                    withField("user")
                            .withDescription("The user as it has been created")
                            .withType("user!")
                }

        registrar.newObject("errors")
                .withDescription("Details of errors with this field")
                .apply {
                    withField("globalErrors")
                            .withDescription("Details of any global errors - i.e. ones that aren't specific to a single field")
                            .withType("[string!]!")
                    withField("fieldErrors")
                            .withDescription("Details of any field-specific errors")
                            .withType("[string!]!")
                }

        registrar.newUnion("mutateUserResult")
                .withDescription("Result of mutating a user")
                .withType("user", UserModel::class)
                .withType("errors", String::class)

        registrar.newMutation("createUser")
                .withDescription("Register a new User")
                .withType("mutateUserResult!")
                .withFetcher(MutationFetcher(UserCreator(userEditor),
                        UserInput::class.java,
                        objectMapper))
                .apply {
                    withArgument("input")
                            .withDescription("The details of the user to create")
                            .withType("newUserInput")
                }
    }
}