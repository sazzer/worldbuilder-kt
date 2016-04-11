package uk.co.grahamcox.worldbuilder.service.worlds

import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.users.UserId
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Mechanism to access Worlds
 */
class WorldFinder {
    private val worlds = listOf(
            World(
                    id = WorldId("abcde"),
                    name = "Discworld",
                    created = ZonedDateTime.of(1983, 11, 24, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant(),
                    updated = ZonedDateTime.of(2015, 8, 27, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant(),
                    ownerId = UserId("12345")
            ),
            World(
                    id = WorldId("abcdf"),
                    name = "Strata",
                    created = ZonedDateTime.of(1981, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant(),
                    updated = ZonedDateTime.of(1981, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant(),
                    ownerId = UserId("12345")
            )
    )
    /**
     * Find the World with the given ID
     * @param id The ID of the World
     * @return the World
     * @throw ResourceNotFoundException if the requested resource couldn't be found
     */
    fun findWorldById(id: WorldId) : World {
        return worlds.firstOrNull { world -> world.id.equals(id) } ?: throw ResourceNotFoundException("Unknown world")
    }

    /**
     * Find all of the worlds in the system
     * @return all of the known worlds
     */
    fun findWorlds() = worlds
}