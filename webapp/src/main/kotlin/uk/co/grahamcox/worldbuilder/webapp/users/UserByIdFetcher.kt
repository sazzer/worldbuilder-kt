package uk.co.grahamcox.worldbuilder.webapp.users

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.users.UserFinder
import uk.co.grahamcox.worldbuilder.service.users.UserId

/**
 * User Fetcher that will fetch a user by it's unique ID
 * @property userFinder The mechanism by which to find users
 */
class UserByIdFetcher(private val userFinder: UserFinder) : DataFetcher {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(UserByIdFetcher::class.java)
    }

    /**
     * Get the User that has been requested
     * @param environment The environment detailing what should be fetched
     * @return the user details
     */
    override fun get(environment: DataFetchingEnvironment): UserModel? {
        val userId = environment.arguments["userId"] ?: throw IllegalArgumentException("No User ID was provided")
        if (userId is String) {
            LOG.debug("Loading user with ID: {}", userId)
            return try {
                val user = userFinder.getById(UserId(userId))
                val userIdentity = user.identity!!
                val result = UserModel(id = userIdentity.id.id,
                        created = userIdentity.created.toString(),
                        updated = userIdentity.updated.toString(),
                        name = user.name,
                        email = user.email,
                        enabled = user.enabled,
                        verified = (user.verificationCode == null))
                LOG.debug("Loaded user: {}", result)
                result
            } catch (e: ResourceNotFoundException) {
                null
            }
        } else {
            throw IllegalArgumentException("The User ID provided was not valid")
        }
    }
}