package uk.co.grahamcox.worldbuilder.service.worlds

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Representation of the ID of a World
 * @property id The actual ID
 */
data class WorldId(@get:JsonValue val id: String)