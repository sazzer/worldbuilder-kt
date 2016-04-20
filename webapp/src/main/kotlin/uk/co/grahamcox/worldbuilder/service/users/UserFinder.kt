package uk.co.grahamcox.worldbuilder.service.users

import uk.co.grahamcox.worldbuilder.service.users.dao.UserDao

/**
 * Mechanism to find user details
 * @property userDao The User DAO to use
 */
class UserFinder(private val userDao: UserDao) {
    /**
     * Get a User by it's unique ID
     * @param userId The ID of the User
     * @return the user record
     */
    fun getUserById(userId: UserId) = userDao.getById(userId)

    /**
     * Get all of the users in the system
     * @return The list of all users
     */
    fun getAllUsers() = userDao.list()
}