package uk.co.grahamcox.worldbuilder.service.users

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Representation of the ID of a User
 * @property id The actual ID
 */
data class UserId(@get:JsonValue val id: String)