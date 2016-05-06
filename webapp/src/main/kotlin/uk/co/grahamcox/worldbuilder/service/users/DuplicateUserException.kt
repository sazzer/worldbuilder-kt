package uk.co.grahamcox.worldbuilder.service.users

/**
 * Exception to indicate that the provided user details are duplicated
 * @param message The error message
 */
class DuplicateUserException(message: String?) : Exception(message)