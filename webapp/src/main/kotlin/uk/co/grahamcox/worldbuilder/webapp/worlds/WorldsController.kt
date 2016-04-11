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
    open fun getWorlds() : JsonApiResponse<Array<JsonApiResource>> {
        val response = JsonApiResponse(
                links = JsonApiTopLevelLinks(
                        self = "/api/worlds"
                ),
                data = arrayOf(
                        JsonApiResource(
                            type = "world",
                            id = "abcde",
                            attributes = mapOf(
                                    "name" to "Discworld",
                                    "created" to Clock.systemUTC().instant(),
                                    "updated" to Clock.systemUTC().instant().plusSeconds(100)
                            ),
                            relationships = mapOf(
                                    "owner" to JsonApiRelationship(
                                            links = JsonApiRelationshipLinks(
                                                    self = "/api/worlds/abcde/relationships/owner",
                                                    related = "/api/worlds/abcde/owner"
                                            ),
                                            data = JsonApiResourceIdentifier(
                                                    type = "user",
                                                    id = 12345
                                            )
                                    )
                            )
                        ),
                        JsonApiResource(
                                type = "world",
                                id = "fghij",
                                attributes = mapOf(
                                        "name" to "Strata",
                                        "created" to Clock.systemUTC().instant(),
                                        "updated" to Clock.systemUTC().instant().plusSeconds(100)
                                ),
                                relationships = mapOf(
                                        "owner" to JsonApiRelationship(
                                                links = JsonApiRelationshipLinks(
                                                        self = "/api/worlds/fghij/relationships/owner",
                                                        related = "/api/worlds/fghij/owner"
                                                ),
                                                data = JsonApiResourceIdentifier(
                                                        type = "user",
                                                        id = 12345
                                                )
                                        )
                                )
                        ),
                        JsonApiResource(
                                type = "world",
                                id = "klmno",
                                attributes = mapOf(
                                        "name" to "Middle Earth",
                                        "created" to Clock.systemUTC().instant(),
                                        "updated" to Clock.systemUTC().instant().plusSeconds(100)
                                ),
                                relationships = mapOf(
                                        "owner" to JsonApiRelationship(
                                                links = JsonApiRelationshipLinks(
                                                        self = "/api/worlds/klmno/relationships/owner",
                                                        related = "/api/worlds/klmno/owner"
                                                ),
                                                data = JsonApiResourceIdentifier(
                                                        type = "user",
                                                        id = 12346
                                                )
                                        )
                                )
                        )
                ),
                included = listOf(
                        JsonApiResource(
                                type = "user",
                                id = 12345,
                                attributes = mapOf(
                                        "name" to "Terry Pratchett"
                                ),
                                links = JsonApiResourceLinks(
                                        self = "/api/users/12345"
                                )
                        ),
                        JsonApiResource(
                                type = "user",
                                id = 12346,
                                attributes = mapOf(
                                        "name" to "J. R. R. Tolkien"
                                ),
                                links = JsonApiResourceLinks(
                                        self = "/api/users/12346"
                                )
                        )
                )
        )

        return response
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
                                relationshipLinkGenerator = { world, user -> "/api/worlds/${world.id}/relationships/owner" },
                                relatedLinkGenerator = { world, user -> "/api/worlds/${world.id}/owner" },
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