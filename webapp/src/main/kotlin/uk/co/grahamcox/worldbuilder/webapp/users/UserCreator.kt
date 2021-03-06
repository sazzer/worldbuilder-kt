package uk.co.grahamcox.worldbuilder.webapp.users

import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.users.DuplicateUserException
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserEditor
import uk.co.grahamcox.worldbuilder.webapp.FetcherException
import uk.co.grahamcox.worldbuilder.webapp.GlobalError
import uk.co.grahamcox.worldbuilder.webapp.MutationFetcher
import java.util.*

/**
 * Data Fetcher that is used to create a new user record
 * @property userEditor The User Editor to create users with
 */
class UserCreator(private val userEditor: UserEditor) : MutationFetcher.MutationHandler<UserInput, UserModel> {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(UserCreator::class.java)
    }

    /**
     * Actually create the user from the details provided
     * @param input The input to create the user from
     * @param environment The environment to get the details to create the user with
     * @return the details of the created user
     */
    override fun process(input: UserInput, environment: DataFetchingEnvironment): UserModel {
        val user = User(
                identity = null,
                name = input.name,
                email = input.email,
                enabled = true,
                verificationCode = UUID.randomUUID().toString()
        )
        val savedUser = try {
            userEditor.saveUser(user)
        } catch (e: DuplicateUserException) {
            throw FetcherException(globalErrors = listOf(
                    GlobalError(errorCode = "DUPLICATE_USER", message = "The user details already exist")
            ))
        }
        val result = UserTranslator.translate(savedUser)
        LOG.debug("Created user: {}", result)

        return result
    }
}