package uk.co.grahamcox.worldbuilder.webapp.users

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserEditor
import java.util.*

/**
 * Data Fetcher that is used to create a new user record
 * @property userEditor The User Editor to create users with
 * @property objectMapper The Object Mapper to decode the input with
 */
class UserCreator(private val userEditor: UserEditor,
                  private val objectMapper: ObjectMapper) : DataFetcher {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(UserCreator::class.java)
    }

    /**
     * Actually create the user from the details provided
     * @param environment The environment to get the details to create the user with
     * @return the details of the created user
     */
    override fun get(environment: DataFetchingEnvironment): Any {
        val input = environment.arguments["input"]

        return when (input) {
            null -> throw IllegalStateException("No user details were provided")
            !is Map<*, *> -> throw IllegalStateException("User details were not a valid object")
            else -> {
                val userInput = objectMapper.convertValue(input, UserInput::class.java)

                val user = User(
                        identity = null,
                        name = userInput.name,
                        email = userInput.email,
                        enabled = true,
                        verificationCode = UUID.randomUUID().toString()
                )
                val savedUser = userEditor.saveUser(user)
                val result = UserTranslator.translate(savedUser)
                LOG.debug("Created user: {}", result)

                mapOf(
                        "clientMutationId" to userInput.clientMutationId,
                        "user" to result
                )
            }
        }

    }
}