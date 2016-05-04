package uk.co.grahamcox.worldbuilder.service.users

/**
 * Mechanism to allow for user records to be created, edited and deleted
 */
interface UserEditor {
    /**
     * Save the given user to the data store
     * @param user The user details to save
     * @return the user as it has been saved.
     */
    fun saveUser(user: User) : User
}