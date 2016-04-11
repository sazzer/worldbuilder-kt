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
@RequestMapping(value = "/api/worlds", produces = arrayOf("application/vnd.api+json"))
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
                relationshipLinkGenerator = { world, user ->
                    MvcUriComponentsBuilder.fromMethod(WorldsController::class.java, WorldsController::getWorldOwnerRelationship.javaMethod, world.id.id)
                            .build()
                            .toUriString()
                },
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
     * Serializer to use for the relationship to the owner
     */
    private val ownerRelationshipSerializer = JsonApiResourceSerializer(
            type = "user",
            idGenerator = World::ownerId,
            selfLinkGenerator = { world ->
                MvcUriComponentsBuilder.fromMethod(WorldsController::class.java, WorldsController::getWorldOwnerRelationship.javaMethod, world.id.id)
                        .build()
                        .toUriString()
            },
            attributeGenerator = mapOf(),
            relatedResources = mapOf()
    )

    /**
     * Get a collection of worlds
     * @return the worlds
     */
    @RequestMapping(method = arrayOf(RequestMethod.GET))
    open fun getWorlds() : JsonApiResponse<List<JsonApiResource>> {
        val worlds = worldFinder.findWorlds()

        return collectionSerializer.serialize(worlds)
    }

    /**
     * Get a single World by ID
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.GET))
    open fun getWorld(@PathVariable("id") id: String) : JsonApiResponse<JsonApiResource> {
        val world = worldFinder.findWorldById(WorldId(id))

        return resourceSerializer.serialize(world)
    }

    /**
     * Get the details of the relationship between a World and the Owner of the World
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping(value = "/{id}/relationships/owner", method = arrayOf(RequestMethod.GET))
    open fun getWorldOwnerRelationship(@PathVariable("id") id: String) : JsonApiResponse<JsonApiResource> {
        var world = worldFinder.findWorldById(WorldId(id))

        return ownerRelationshipSerializer.serialize(world)
    }

    /**
     * Get the details of the relationship between a World and the Owner of the World
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping(value = "/{id}/relationships/owner", method = arrayOf(RequestMethod.PATCH))
    @ResponseStatus(HttpStatus.FORBIDDEN)
    open fun changeWorldOwnerRelationship(@PathVariable("id") id: String) = mapOf(
            "status" to HttpStatus.FORBIDDEN.value().toString(),
            "title" to "The owner of a World cannot be changed"
    )

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