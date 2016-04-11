package uk.co.grahamcox.worldbuilder.webapp.worlds

import org.springframework.hateoas.mvc.ControllerLinkBuilder
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.co.grahamcox.worldbuilder.webapp.jsonapi.response.*
import java.time.Clock

/**
 * Controller for interacting with World records
 */
@RestController
@RequestMapping("/api/worlds")
open class WorldsController(private val resourceSerializer: JsonApiResourceSerializer<World>,
                            private val collectionSerializer: JsonApiCollectionSerializer<List<World>, World>) {
    /**
     * Get a collection of worlds
     * @return the worlds
     */
    @RequestMapping(produces = arrayOf("application/vnd.api+json"))
    open fun getWorlds() : JsonApiResponse<List<JsonApiResource>> {
        val worlds = listOf(
                World(id = "abcde",
                        name = "Discworld",
                        created = Clock.systemUTC().instant(),
                        updated = Clock.systemUTC().instant().plusSeconds(100)),
                World(id = "fghij",
                        name = "Strata",
                        created = Clock.systemUTC().instant(),
                        updated = Clock.systemUTC().instant().plusSeconds(100))
        )

        return collectionSerializer.serialize(worlds)
    }

    /**
     * Get a single World by ID
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping(value = "/{id}", produces = arrayOf("application/vnd.api+json"))
    open fun getWorld(@PathVariable("id") id: String) : JsonApiResponse<JsonApiResource> {
        val world = World(id = id,
                name = "Discworld",
                created = Clock.systemUTC().instant(),
                updated = Clock.systemUTC().instant().plusSeconds(100))

        return resourceSerializer.serialize(world)
    }
}