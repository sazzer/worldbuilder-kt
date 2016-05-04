package uk.co.grahamcox.worldbuilder.webapp.spring

import graphql.schema.DataFetcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.service.users.UserFinder
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistrar
import uk.co.grahamcox.worldbuilder.webapp.graphql.GraphQLRegistration
import uk.co.grahamcox.worldbuilder.webapp.users.UserByIdFetcher
import uk.co.grahamcox.worldbuilder.webapp.users.UserModel
import java.time.Instant

/**
 * Spring Configuration for the API layer to work with Users records
 */
@Configuration
open class UsersConfig {
    /**
     * The Data Fetcher for Users by ID
     */
    @Autowired
    @Bean
    open fun userByIdFetcher(userFinder : UserFinder) = UserByIdFetcher(userFinder)

    /**
     * Configure the User parts of the Schema
     */
    @Autowired
    @Bean
    open fun userSchema(userByIdFetcher: UserByIdFetcher) = object : GraphQLRegistration {
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
                        withField("name")
                                .withDescription("The name of the User")
                                .withType("string!")
                        withField("email")
                                .withDescription("The email address of the User")
                                .withType("string")
                    }

            registrar.newMutation("createUser")
                    .withDescription("Register a new User")
                    .withType("user")
                    .withFetcher(DataFetcher { e ->
                        val input = e.arguments["user"] as Map<String, Any?>

                        UserModel(
                                id = "newUser",
                                created = Instant.now(),
                                updated = Instant.now(),
                                name = input["name"] as String,
                                email = input["email"] as String?,
                                enabled = true,
                                verified = false
                        )
                    })
                    .withArgument("user")
                            .withDescription("The details of the user to create")
                            .withType("newUserInput")
        }
    }
}