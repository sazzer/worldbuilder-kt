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
open class WorldsController {
    /**
     * Get a collection of worlds
     * @return the worlds
     */
    @RequestMapping
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

        val serializer = JsonApiCollectionSerializer(
                type = "world",
                resourceListGenerator = { worlds: List<World> -> worlds },
                idGenerator = World::id,
                attributeGenerator = mapOf(
                        "name" to World::name,
                        "created" to World::created,
                        "updated" to World::updated
                ),
                collectionSelfLinkGenerator = { worlds -> "/api/worlds" },
                resourceSelfLinkGenerator = { world -> "/api/worlds/${world.id}" },
                relatedResources = mapOf(
                        "owner" to JsonApiRelatedResourceSchema(
                                type = "user",
                                resourceExtractor = { world -> User(id = 12345, name = "Terry Pratchett") },
                                idGenerator = User::id,
                                relationshipLinkGenerator = { world, user -> "/api/worlds/${world.id}/relationships/owner" },
                                relatedLinkGenerator = { world, user -> "/api/worlds/${world.id}/owner" },
                                selfLinkGenerator = { world, user -> "/api/users/${user.id}" },
                                attributeGenerator = mapOf(
                                        "name" to User::name
                                )
                        ),
                        "lastEdited" to JsonApiRelatedResourceSchema(
                                type = "user",
                                resourceExtractor = { world -> User(id = 12345, name = "Terry Pratchett") },
                                idGenerator = User::id,
                                relationshipLinkGenerator = { world, user -> "/api/worlds/${world.id}/relationships/edited" },
                                relatedLinkGenerator = { world, user -> "/api/worlds/${world.id}/edited" },
                                selfLinkGenerator = { world, user -> "/api/users/${user.id}" },
                                attributeGenerator = mapOf(
                                        "name" to User::name
                                )
                        )

                )
        )

        return serializer.serialize(worlds)
    }

    /**
     * Get a single World by ID
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping("/{id}")
    open fun getWorld(@PathVariable("id") id: String) : JsonApiResponse<JsonApiResource> {
        val world = World(id = id,
                name = "Discworld",
                created = Clock.systemUTC().instant(),
                updated = Clock.systemUTC().instant().plusSeconds(100))

        val serializer = JsonApiResourceSerializer(
                type = "world",
                idGenerator = World::id,
                attributeGenerator = mapOf(
                        "name" to World::name,
                        "created" to World::created,
                        "updated" to World::updated
                ),
                selfLinkGenerator = { world -> "/api/worlds/${world.id}" },
                relatedResources = mapOf(
                        "owner" to JsonApiRelatedResourceSchema(
                                type = "user",
                                resourceExtractor = { world -> User(id = 12345, name = "Terry Pratchett") },
                                idGenerator = User::id,
                                relationshipLinkGenerator = { world, user -> "/api/worlds/${world.id}/relationships/owner" },
                                relatedLinkGenerator = { world, user -> "/api/worlds/${world.id}/owner" },
                                selfLinkGenerator = { world, user -> "/api/users/${user.id}" },
                                attributeGenerator = mapOf(
                                        "name" to User::name
                                )
                        ),
                        "lastEdited" to JsonApiRelatedResourceSchema(
                                type = "user",
                                resourceExtractor = { world -> User(id = 12345, name = "Terry Pratchett") },
                                idGenerator = User::id,
                                relationshipLinkGenerator = { world, user -> "/api/worlds/${world.id}/relationships/edited" },
                                relatedLinkGenerator = { world, user -> "/api/worlds/${world.id}/edited" },
                                selfLinkGenerator = { world, user -> "/api/users/${user.id}" },
                                attributeGenerator = mapOf(
                                        "name" to User::name
                                )
                        )

                )
        )

        return serializer.serialize(world)
    }
}