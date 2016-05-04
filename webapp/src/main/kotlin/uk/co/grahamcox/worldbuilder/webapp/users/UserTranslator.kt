package uk.co.grahamcox.worldbuilder.webapp.users

import uk.co.grahamcox.worldbuilder.service.users.User

/**
 * Translator to translate between API and Service versions of a User
 */
object UserTranslator {
    /**
     * Translate the Service version of a User into the API Version
     * @param user The Service version to translate
     * @return the translated API version
     */
    fun translate(user: User) : UserModel {
        val userIdentity = user.identity!!
        val result = UserModel(id = userIdentity.id.id,
                created = userIdentity.created,
                updated = userIdentity.updated,
                name = user.name,
                email = user.email,
                enabled = user.enabled,
                verified = (user.verificationCode == null))
        return result
    }
}