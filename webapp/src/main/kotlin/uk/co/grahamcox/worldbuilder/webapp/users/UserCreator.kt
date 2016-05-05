package uk.co.grahamcox.worldbuilder.webapp.users

import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserEditor
import uk.co.grahamcox.worldbuilder.webapp.MutationFetcher
import java.util.*

/**
 * Data Fetcher that is used to create a new user record
 * @property userEditor The User Editor to create users with
 */
class UserCreator(private val userEditor: UserEditor) : MutationFetcher.MutationHandler<UserInput, Map<String, Any>> {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(UserCreator::class.java)
    }

    /** The input model */
    override val inputModel = UserInput::class.java

    /**
     * Actually create the user from the details provided
     * @param input The input to create the user from
     * @param environment The environment to get the details to create the user with
     * @return the details of the created user
     */
    override fun process(input: UserInput, environment: DataFetchingEnvironment): Map<String, Any>? {
        val user = User(
                identity = null,
                name = input.name,
                email = input.email,
                enabled = true,
                verificationCode = UUID.randomUUID().toString()
        )
        val savedUser = userEditor.saveUser(user)
        val result = UserTranslator.translate(savedUser)
        LOG.debug("Created user: {}", result)

        return mapOf(
                "clientMutationId" to input.clientMutationId,
                "user" to result
        )
    }
}