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
     * Get a single World by ID
     * @param id The ID of the World
     * @return the world
     */
    @RequestMapping("/{id}")
    open fun getWorld(@PathVariable("id") id: String) : JsonApiResponse {
        val response = JsonApiResponse(
                links = JsonApiTopLevelLinks(
                        self = "/api/worlds/${id}"
                ),
                data = JsonApiResource(
                        type = "world",
                        id = id,
                        attributes = mapOf(
                                "name" to "Discworld",
                                "created" to Clock.systemUTC().instant(),
                                "updated" to Clock.systemUTC().instant().plusSeconds(100)
                        ),
                        relationships = mapOf(
                                "owner" to JsonApiRelationship(
                                        links = JsonApiRelationshipLinks(
                                                self = "/api/worlds/${id}/relationships/owner",
                                                related = "/api/worlds/${id}/owner"
                                        ),
                                        data = JsonApiResourceIdentifier(
                                                type = "user",
                                                id = 12345
                                        )
                                )
                        )
                ),
                included = arrayOf(
                        JsonApiResource(
                                type = "user",
                                id = 12345,
                                attributes = mapOf(
                                        "name" to "Terry Pratchett"
                                ),
                                links = JsonApiResourceLinks(
                                        self = "/api/users/12345"
                                )
                        )
                )
        )

        return response
    }
}