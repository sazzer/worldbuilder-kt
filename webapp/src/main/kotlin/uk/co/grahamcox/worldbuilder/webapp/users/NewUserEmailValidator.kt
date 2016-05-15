package uk.co.grahamcox.worldbuilder.webapp.users

import uk.co.grahamcox.worldbuilder.webapp.Error
import uk.co.grahamcox.worldbuilder.webapp.FieldError
import uk.co.grahamcox.worldbuilder.webapp.RequestValidator

/**
 * Validator to ensure that the email address is valid
 */
class NewUserEmailValidator : RequestValidator<UserInput> {
    companion object {
        /** Regex for matching an email address */
        val EMAIL_REGEX = """(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])""".toRegex()
    }

    /**
     * Validate the email address of the user to create
     * @param input The input object
     * @return the errors, if any
     */
    override fun validate(input: UserInput): Collection<Error> {
        return if (!input.email.matches(EMAIL_REGEX)) {
            listOf(
                    FieldError(errorCode = "INVALID_EMAIL", field = "email", message = "The email address was invalid")
            )
        } else {
            listOf()
        }
    }
}