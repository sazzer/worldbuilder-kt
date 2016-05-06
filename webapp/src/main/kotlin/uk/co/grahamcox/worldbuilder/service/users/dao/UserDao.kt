package uk.co.grahamcox.worldbuilder.service.users.dao

import uk.co.grahamcox.worldbuilder.service.Identity
import uk.co.grahamcox.worldbuilder.service.dao.BaseDao
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserId

/**
 * Mechanism to access User records in the database
 */
interface UserDao : BaseDao<UserId, User> {
    /**
     * Attempt to find the user that has the given email address
     * @param email The email address to search for
     * @return the user with this email address if found. May be null if one wasn't found
     */
    fun findByEmail(email: String) : User?
}