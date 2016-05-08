package uk.co.grahamcox.worldbuilder.webapp.users

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Input object for the new user creation
 * @property name The name of the user to create
 * @property email The email address of the user to create
 */
data class UserInput @JsonCreator constructor(@JsonProperty("name") val name: String,
                                              @JsonProperty("email") val email: String)