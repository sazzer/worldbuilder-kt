package uk.co.grahamcox.worldbuilder.service.users.dao

import uk.co.grahamcox.worldbuilder.service.Identity
import uk.co.grahamcox.worldbuilder.service.dao.BaseDao
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserId

/**
 * Mechanism to access User records in the database
 */
interface UserDao : BaseDao<UserId, User>