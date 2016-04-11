package uk.co.grahamcox.worldbuilder.service.worlds

import uk.co.grahamcox.worldbuilder.service.users.UserId
import java.time.Instant

/**
 * Representation of an actual World
 * @property id The ID of the World
 * @property name The name of the World
 * @property created When the World was created
 * @property updated When the World was last updated
 * @property ownerId The ID of the Owner of the World
 */
data class World(val id: WorldId,
                 val name: String,
                 val created: Instant,
                 val updated: Instant,
                 val ownerId: UserId)