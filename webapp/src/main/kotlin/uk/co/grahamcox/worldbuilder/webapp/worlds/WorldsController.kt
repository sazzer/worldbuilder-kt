package uk.co.grahamcox.worldbuilder.webapp.worlds

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.worlds.World
import uk.co.grahamcox.worldbuilder.service.worlds.WorldFinder
import uk.co.grahamcox.worldbuilder.service.worlds.WorldId
import uk.co.grahamcox.worldbuilder.webapp.IdGenerator
import uk.co.grahamcox.worldbuilder.webapp.InvalidIdException
import uk.co.grahamcox.worldbuilder.webapp.api.*
import uk.co.grahamcox.worldbuilder.webapp.api.utils.PaginationModel
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.SwaggerResponse
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.SwaggerResponses
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.SwaggerSummary
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.SwaggerTags

/**
 * Controller for interacting with World records
 * @property worldFinder The mechanism to find worlds with
 * @property idGenerator The mechanism to generate IDs with
 */
@RestController
@RequestMapping(value = "/api/worlds", produces = arrayOf("application/json"))
@SwaggerTags(arrayOf("worlds"))
open class WorldsController(private val worldFinder: WorldFinder,
                            private val idGenerator: IdGenerator) {
    /**
     * Get a single World by ID
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.GET))
    @SwaggerSummary("Get a single world by ID")
    @SwaggerResponses(arrayOf(
            SwaggerResponse(statusCode = HttpStatus.OK, description = "The details of the World", schema = "world.json"),
            SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "Something about the request was invalid", schema = "simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.NOT_FOUND, description = "The requested World wasn't found", schema = "simpleError.json")
    ))
    open fun getWorld(@PathVariable("id") @SwaggerSummary("The ID of the World") id: String) : WorldModel {
        val rawId = idGenerator.parseId(id, WorldId::class)
        val world = worldFinder.findWorldById(rawId)

        val result = translateWorld(world)

        return result
    }

    /**
     * Perform a search across all of the known worlds
     * @return the worlds that matched
     */
    @RequestMapping
    @SwaggerSummary("Retrieve a potentially filtered list of all the known worlds")
    @SwaggerResponse(statusCode = HttpStatus.OK, description = "The details of the matching worlds", schema = "worlds.json")
    open fun listWorlds() : WorldsModel {
        val worlds = worldFinder.findWorlds()

        val result = WorldsModel()
            .withResults(worlds.map { world -> translateWorld(world) })
            .withPagination(PaginationModel()
                .withOffset(0)
                .withTotalResults(worlds.size))

        return result
    }

    /**
     * Translate the given World into a response object
     * @param world The world to translate
     * @return the translated world
     */
    fun translateWorld(world: World) = WorldModel()
            .withId(idGenerator.generateId(world.id))
            .withName(world.name)
            .withCreated(world.created)
            .withUpdated(world.updated)
            .withWorldEmbedded(WorldEmbeddedModel()
                    .withOwner(UserBriefModel()
                            .withId(idGenerator.generateId(world.ownerId))
                            .withName("Terry Pratchett"))
            )

    /**
     * Handler for when we try to load an unknown resource
     * @param e The exception to handle
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleResourceNotFoundException(e: ResourceNotFoundException) = SimpleErrorModel()
            .withStatusCode(HttpStatus.NOT_FOUND.value())
            .withErrorCode("RESOURCE_NOT_FOUND")
            .withReason("The World with the given ID was not found")

    /**
     * Handler for when we fail to parse an ID
     * @param e The exception to handle
     */
    @ExceptionHandler(InvalidIdException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidIdException(e: InvalidIdException) = SimpleErrorModel()
            .withStatusCode(HttpStatus.BAD_REQUEST.value())
            .withErrorCode("INVALID_ID")
            .withReason("The provided ID was not valid: ${e.id}")
}