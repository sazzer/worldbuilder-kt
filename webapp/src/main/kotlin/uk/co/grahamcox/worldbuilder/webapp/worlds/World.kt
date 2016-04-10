package uk.co.grahamcox.worldbuilder.webapp.worlds

import java.time.Instant

/**
 * Representation of an actual World
 * @param id the ID of the world
 * @param name The name of the world
 * @param created When the world was created
 * @param updated When the world was updated
 */
data class World(val id: String,
                 val name: String,
                 val created: Instant,
                 val updated: Instant)