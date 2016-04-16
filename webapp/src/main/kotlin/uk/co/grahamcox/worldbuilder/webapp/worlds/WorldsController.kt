package uk.co.grahamcox.worldbuilder.webapp.worlds

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.worlds.WorldFinder
import uk.co.grahamcox.worldbuilder.service.worlds.WorldId
import uk.co.grahamcox.worldbuilder.webapp.api.UserBriefModel
import uk.co.grahamcox.worldbuilder.webapp.api.WorldModel
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.SwaggerSummary
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.SwaggerTags

/**
 * Controller for interacting with World records
 */
@RestController
@RequestMapping(value = "/api/worlds", produces = arrayOf("application/json"))
@SwaggerTags(arrayOf("worlds"))
open class WorldsController(private val worldFinder: WorldFinder) {
    /**
     * Get a single World by ID
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.GET))
    @SwaggerSummary("Get a single world by ID")
    open fun getWorld(@PathVariable("id") @SwaggerSummary("The ID of the World") id: String) : WorldModel {
        val world = worldFinder.findWorldById(WorldId(id))

        val result = WorldModel()
            .withName(world.name)
            .withCreated(world.created.toString())
            .withUpdated(world.updated.toString())
            .withOwner(UserBriefModel()
                    .withId(world.ownerId.id)
                    .withName("Terry Pratchett"))

        return result
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