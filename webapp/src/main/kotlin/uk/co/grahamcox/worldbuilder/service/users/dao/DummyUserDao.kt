package uk.co.grahamcox.worldbuilder.service.users.dao

import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserId
import java.time.Clock

/**
 * Dummy implementation of the User DAO that works in terms of an in-memory map
 */
class DummyUserDao : UserDao {
    private val data = mutableListOf(
            User(
                    id = UserId("12345"),
                    created = Clock.systemUTC().instant(),
                    updated = Clock.systemUTC().instant(),
                    name = "Terry Pratchett",
                    email = "not@set.com",
                    enabled = true,
                    verificationCode = null
            ),
            User(
                    id = UserId("12346"),
                    created = Clock.systemUTC().instant(),
                    updated = Clock.systemUTC().instant(),
                    name = "Graham Cox",
                    email = "graham@grahamcox.co.uk",
                    enabled = true,
                    verificationCode = "abcdef"
            )
    )
    /**
     * Get the user that has the given ID
     * @param id The ID of the user
     * @return the user with the given ID
     * @throws uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException if the user with the given ID doesn't exist
     */
    override fun getById(id: UserId) =
        data.firstOrNull { user -> id.equals(user.id) }
            ?: throw ResourceNotFoundException(id)

    /**
     * List all of the known users
     * @return the list of all known users
     */
    override fun list() : List<User> = data

    /**
     * Delete the given user
     * @param user The user to delete
     */
    override fun delete(user: User) {
        data.remove(user)
    }

    /**
     * Save the given user
     * @param user The user details to save. If the user has an ID then it will update what is in the data store. If it
     * does not have an ID then a new user will be created.
     * @return The newly saved user details
     */
    override fun save(user: User) : User {
        throw UnsupportedOperationException()
    }
}