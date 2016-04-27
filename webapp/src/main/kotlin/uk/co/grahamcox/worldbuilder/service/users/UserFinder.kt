package uk.co.grahamcox.worldbuilder.service.users

/**
 * Mechanism to find user records
 */
interface UserFinder {
    /**
     * Find the user that has the given User ID
     * @param id The ID of the user to find
     * @return the user
     */
    fun getById(id: UserId) : User
}