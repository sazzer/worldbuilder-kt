package uk.co.grahamcox.worldbuilder.webapp.worlds

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.worlds.World
import uk.co.grahamcox.worldbuilder.service.worlds.WorldFinder
import uk.co.grahamcox.worldbuilder.service.worlds.WorldId
import uk.co.grahamcox.worldbuilder.webapp.jsonapi.response.*
import kotlin.reflect.jvm.javaMethod

/**
 * Controller for interacting with World records
 */
@RestController
@RequestMapping("/api/worlds")
open class WorldsController(private val worldFinder: WorldFinder) {
    companion object {
        /** The resource type for the worlds resources */
        private val RESOURCE_TYPE = "world"

        /** The generator of the ID of a World */
        private val RESOURCE_ID_GENERATOR = World::id

        /** The attributes of a World */
        private val RESOURCE_ATTRIBUTES = mapOf(
                "name" to World::name,
                "created" to World::created,
                "updated" to World::updated
        )

        /** The Self Link of a World */
        private val RESOURCE_SELF_LINK_GENERATOR = { world: World ->
            MvcUriComponentsBuilder.fromMethod(WorldsController::class.java, WorldsController::getWorld.javaMethod, world.id.id)
                    .build()
                    .toUriString()
        }

        /** The relationship representing the owner of a world */
        private val OWNER_RELATIONSHIP_SCHEMA = JsonApiRelatedResourceSchema<World, User>(
                type = "user",
                resourceExtractor = { world -> User(id = world.ownerId, name = "Terry Pratchett") },
                idGenerator = User::id,
                relationshipLinkGenerator = { world, user -> "/api/worlds/${world.id.id}/relationships/owner" },
                relatedLinkGenerator = { world, user -> "/api/worlds/${world.id.id}/owner" },
                selfLinkGenerator = { world, user -> "/api/users/${user.id.id}" },
                attributeGenerator = mapOf(
                        "name" to User::name
                )
        )

        /** The complete set of relationships for a world */
        private val RESOURCE_RELATIONSHIPS = mapOf(
                "owner" to OWNER_RELATIONSHIP_SCHEMA
        )
    }

    /**
     * Serializer to use for individual resources
     */
    private val resourceSerializer = JsonApiResourceSerializer(
            type = RESOURCE_TYPE,
            idGenerator = RESOURCE_ID_GENERATOR,
            attributeGenerator = RESOURCE_ATTRIBUTES,
            selfLinkGenerator = RESOURCE_SELF_LINK_GENERATOR,
            relatedResources = RESOURCE_RELATIONSHIPS
    )

    /**
     * Serializer to use for collections of resources
     */
    private val collectionSerializer = JsonApiCollectionSerializer(
            type = RESOURCE_TYPE,
            resourceListGenerator = { worlds: List<World> -> worlds },
            idGenerator = RESOURCE_ID_GENERATOR,
            attributeGenerator = RESOURCE_ATTRIBUTES,
            collectionSelfLinkGenerator = { worlds ->
                MvcUriComponentsBuilder.fromMethod(WorldsController::class.java, WorldsController::getWorlds.javaMethod)
                        .build()
                        .toUriString()
            },
            resourceSelfLinkGenerator = RESOURCE_SELF_LINK_GENERATOR,
            relatedResources = RESOURCE_RELATIONSHIPS
    )

    /**
     * Get a collection of worlds
     * @return the worlds
     */
    @RequestMapping(produces = arrayOf("application/vnd.api+json"))
    open fun getWorlds() : JsonApiResponse<List<JsonApiResource>> {
        val worlds = worldFinder.findWorlds()

        return collectionSerializer.serialize(worlds)
    }

    /**
     * Get a single World by ID
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping(value = "/{id}", produces = arrayOf("application/vnd.api+json"))
    open fun getWorld(@PathVariable("id") id: String) : JsonApiResponse<JsonApiResource> {
        val world = worldFinder.findWorldById(WorldId(id))

        return resourceSerializer.serialize(world)
    }

    /**
     * Handler for when we try to load an unknown resource
     * @param e The exception to handle
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFoundException(e: ResourceNotFoundException) = mapOf(
            "status" to HttpStatus.NOT_FOUND.value().toString(),
            "title" to e.message
    )
}