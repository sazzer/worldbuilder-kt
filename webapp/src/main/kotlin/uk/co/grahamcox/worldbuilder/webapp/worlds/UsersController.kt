package uk.co.grahamcox.worldbuilder.webapp.worlds

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import uk.co.grahamcox.worldbuilder.service.users.UserId
import uk.co.grahamcox.worldbuilder.webapp.IdGenerator
import uk.co.grahamcox.worldbuilder.webapp.api.users.ProfileModel
import uk.co.grahamcox.worldbuilder.webapp.api.users.UserModel
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.*
import java.time.Clock

/**
 * Controller for interacting with User records
 * @property idGenerator The mechanism to generate IDs with
 */
@RestController
@RequestMapping(value = "/api/users", produces = arrayOf("application/json"))
@SwaggerTags(arrayOf("users"))
open class UsersController(private val idGenerator: IdGenerator) {
    /**
     * Get a single User by ID
     * @param id The ID of the User
     * @return the user
     */
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.GET))
    @SwaggerSummary("Get a single user by ID")
    @SwaggerResponses(arrayOf(
            SwaggerResponse(statusCode = HttpStatus.OK, description = "The details of the User", schema = "users/user.json"),
            SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "Something about the request was invalid", schema = "simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.NOT_FOUND, description = "The requested User wasn't found", schema = "simpleError.json")
    ))
    open fun getUserById(@PathVariable("id") @SwaggerSummary("The ID of the User") id: String) : UserModel {
        val result = UserModel()
            .withId(idGenerator.generateId(UserId("12345")))
            .withCreated(Clock.systemUTC().instant())
            .withUpdated(Clock.systemUTC().instant())
            .withEnabled(true)
            .withVerified(true)
            .withProfile(ProfileModel()
                .withName("Terry Pratchett")
                .withEmail("not@set.com"))
        return result
    }

    /**
     * Create a new user
     * @param userDetails The user details to create
     * @return the newly created user
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST))
    @SwaggerSummary("Create a new user")
    @SwaggerResponses(arrayOf(
            SwaggerResponse(statusCode = HttpStatus.OK, description = "The details of the User", schema = "users/user.json"),
            SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "Something about the request was invalid", schema = "simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.CONFLICT, description = "The user details are duplicates of existing data", schema = "simpleError.json")
    ))
    @SwaggerRequest(description = "The profile details of the user to create", schema = "users/profile.json")
    open fun createUser(@RequestBody userDetails: ProfileModel) : UserModel {
        val result = UserModel()
                .withId(idGenerator.generateId(UserId("12345")))
                .withCreated(Clock.systemUTC().instant())
                .withUpdated(Clock.systemUTC().instant())
                .withEnabled(true)
                .withVerified(false)
                .withProfile(userDetails)
        return result
    }
}
