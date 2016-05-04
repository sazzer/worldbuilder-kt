package uk.co.grahamcox.worldbuilder.webapp.users

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserEditor
import java.util.*

/**
 * Data Fetcher that is used to create a new user record
 * @property userEditor The User Editor to create users with
 */
class UserCreator(private val userEditor: UserEditor) : DataFetcher {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(UserCreator::class.java)
    }

    /**
     * Actually create the user from the details provided
     * @param environment The environment to get the details to create the user with
     * @return the details of the created user
     */
    override fun get(environment: DataFetchingEnvironment): UserModel {
        val input = environment.arguments["user"]

        return when (input) {
            null -> throw IllegalStateException("No user details were provided")
            !is Map<*, *> -> throw IllegalStateException("User details were not a valid object")
            else -> {
                val name = input.get("name")
                val email = input.get("email")

                if (name !is String) {
                    throw IllegalStateException("User name was not a valid string")
                }
                if (email != null && email !is String) {
                    throw IllegalStateException("User Email was not a valid string")
                }

                val user = User(
                        identity = null,
                        name = name,
                        email = email as String?,
                        enabled = true,
                        verificationCode = UUID.randomUUID().toString()
                )
                val savedUser = userEditor.saveUser(user)
                val result = UserTranslator.translate(savedUser)
                LOG.debug("Created user: {}", result)

                result
            }
        }

    }
}