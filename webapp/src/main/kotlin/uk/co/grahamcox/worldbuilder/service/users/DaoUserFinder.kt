package uk.co.grahamcox.worldbuilder.service.users

import uk.co.grahamcox.worldbuilder.service.users.dao.UserDao

/**
 * User Finder that works directly in terms of the User DAO
 */
class DaoUserFinder(private val userDao: UserDao) : UserFinder {
    /**
     * Find the user that has the given User ID
     * @param id The ID of the user to find
     * @return the user
     */
    override fun getById(id: UserId): User = userDao.getById(id)
}