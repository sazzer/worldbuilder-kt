package uk.co.grahamcox.worldbuilder.service.users

import uk.co.grahamcox.worldbuilder.service.users.dao.UserDao

/**
 * Implementation of the User Editor that works directly in terms of the User DAO
 * @property userDao The User DAO to use
 */
class DaoUserEditor(private val userDao: UserDao) : UserEditor {
    /**
     * Save the given user to the data store
     * @param user The user details to save
     * @return the user as it has been saved.
     */
    override fun saveUser(user: User) = userDao.save(user)
}