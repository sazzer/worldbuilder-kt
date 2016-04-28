package uk.co.grahamcox.worldbuilder.webapp.users

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserFinder
import uk.co.grahamcox.worldbuilder.service.users.UserId
import uk.co.grahamcox.worldbuilder.webapp.EtagGenerator
import uk.co.grahamcox.worldbuilder.webapp.IdGenerator
import uk.co.grahamcox.worldbuilder.webapp.api.users.NewUserModel
import uk.co.grahamcox.worldbuilder.webapp.api.users.ProfileModel
import uk.co.grahamcox.worldbuilder.webapp.api.users.StatusModel
import uk.co.grahamcox.worldbuilder.webapp.api.users.UserModel
import uk.co.grahamcox.worldbuilder.webapp.swagger.annotations.*

/**
 * Controller for working with user records
 * @property userFinder The user finder to use
 */
@RestController
@SwaggerTags(arrayOf("users"))
@RequestMapping("/api/users")
open class UsersController(private val userFinder: UserFinder,
                           private val idGenerator: IdGenerator) {
    /**
     * Get a single User by the Unique User ID
     * @param id The ID of the user
     * @return the user that was found
     */
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.GET))
    @SwaggerSummary("Get a single User by ID")
    @SwaggerResponses(arrayOf(
        SwaggerResponse(statusCode = HttpStatus.OK, description = "A user is successfully retrieved", schema = "users/user.json"),
        SwaggerResponse(statusCode = HttpStatus.UNAUTHORIZED, description = "The request is not correctly authorized", schema = "errors/simpleError.json"),
        SwaggerResponse(statusCode = HttpStatus.FORBIDDEN, description = "The request is not allowed to view this user", schema = "errors/simpleError.json"),
        SwaggerResponse(statusCode = HttpStatus.NOT_FOUND, description = "The requested user was not found", schema = "errors/simpleError.json"),
        SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "The request was badly formed", schema = "errors/validationError.json")
    ))
    fun getUserById(@PathVariable("id") @SwaggerSummary("The ID of the user to retrieve") id: String): ResponseEntity<UserModel> {
        val user = userFinder.getById(UserId(id))

        val result = buildResponseEntity(user)

        return result
    }

    /**
     * Build the API representation of an internal User
     * @param user The User to translate
     * @return the API translation
     */
    fun translateUserToAPI(user: User) = UserModel()
            .withId(user.identity?.let { idGenerator.generateId(it.id) })
            .withCreated(user.identity?.created)
            .withProfile(ProfileModel()
                    .withName(user.name)
                    .withEmail(user.email))
            .withStatus(StatusModel()
                    .withBanned(!user.enabled)
                    .withVerified(user.verificationCode == null))
            .withLogins(listOf(
            ))

    fun buildResponseEntity(user: User) : ResponseEntity<UserModel> {
        val userModel = translateUserToAPI(user)

        val headers = HttpHeaders()
        user.identity?.apply {
            headers.lastModified = updated.toEpochMilli()
            headers.eTag = EtagGenerator.generateEtag(user.identity)
        }

        val result = ResponseEntity(userModel, headers, HttpStatus.OK)
        return result
    }

    /**
     * Search for users in the system
     */
    @RequestMapping(method = arrayOf(RequestMethod.GET))
    @SwaggerSummary("Search users")
    @SwaggerResponses(arrayOf(
            SwaggerResponse(statusCode = HttpStatus.OK, description = "The search results. May return an empty resultset", schema = "users/users.json"),
            SwaggerResponse(statusCode = HttpStatus.UNAUTHORIZED, description = "The request is not correctly authorized", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.FORBIDDEN, description = "The request is not allowed to view this user", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "The request was badly formed", schema = "errors/validationError.json")
    ))
    fun searchUsers() {

    }

    /**
     * Create a new user in the system
     * @param user The user details to save
     */
    @RequestMapping(method = arrayOf(RequestMethod.POST))
    @SwaggerSummary("Create a single User")
    @SwaggerResponses(arrayOf(
            SwaggerResponse(statusCode = HttpStatus.CREATED, description = "A user is successfully created", schema = "users/user.json"),
            SwaggerResponse(statusCode = HttpStatus.UNAUTHORIZED, description = "The request is not correctly authorized", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.FORBIDDEN, description = "The request is not allowed to create a user", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.CONFLICT, description = "The requested user details are not unique", schema = "errors/validationError.json"),
            SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "The request was badly formed", schema = "errors/validationError.json")
    ))
    @SwaggerRequest(description = "The details of the user to create", schema = "users/newUser.json")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody user: NewUserModel) {

    }

    /**
     * Edit an existing user in the system
     * @param id The ID of the user
     * @param user The user details to save
     */
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.PUT))
    @SwaggerSummary("Edit a single User")
    @SwaggerResponses(arrayOf(
            SwaggerResponse(statusCode = HttpStatus.OK, description = "A user is successfully updated", schema = "users/user.json"),
            SwaggerResponse(statusCode = HttpStatus.UNAUTHORIZED, description = "The request is not correctly authorized", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.FORBIDDEN, description = "The request is not allowed to edit this user", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.NOT_FOUND, description = "The requested user was not found", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.CONFLICT, description = "The requested user details are not unique", schema = "errors/validationError.json"),
            SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "The request was badly formed", schema = "errors/validationError.json")
    ))
    @SwaggerRequest(description = "The details of the user to save", schema = "users/user.json")
    fun editUser(@PathVariable("id") @SwaggerSummary("The ID of the user to update") id: String,
                 @RequestBody user: UserModel) {

    }

    /**
     * Edit an existing user in the system
     * @param id The ID of the user
     * @param user The user details to save
     */
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.PATCH))
    @SwaggerSummary("Edit a single User")
    @SwaggerResponses(arrayOf(
            SwaggerResponse(statusCode = HttpStatus.OK, description = "A user is successfully updated", schema = "users/user.json"),
            SwaggerResponse(statusCode = HttpStatus.UNAUTHORIZED, description = "The request is not correctly authorized", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.FORBIDDEN, description = "The request is not allowed to edit this user", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.NOT_FOUND, description = "The requested user was not found", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.CONFLICT, description = "The requested user details are not unique", schema = "errors/validationError.json"),
            SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "The request was badly formed", schema = "errors/validationError.json")
    ))
    @SwaggerRequest(description = "The details of the user to save", schema = "utils/patch.json")
    fun patchUser(@PathVariable("id") @SwaggerSummary("The ID of the user to update") id: String,
                  @RequestBody user: UserModel) {

    }

    /**
     * Delete an existing user in the system
     * @param id The ID of the user
     */
    @RequestMapping(value = "/{id}", method = arrayOf(RequestMethod.DELETE))
    @SwaggerSummary("Delete a single User")
    @SwaggerResponses(arrayOf(
            SwaggerResponse(statusCode = HttpStatus.NO_CONTENT, description = "A user is successfully deleted", schema = "users/user.json"),
            SwaggerResponse(statusCode = HttpStatus.UNAUTHORIZED, description = "The request is not correctly authorized", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.FORBIDDEN, description = "The request is not allowed to delete this user", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.NOT_FOUND, description = "The requested user was not found", schema = "errors/simpleError.json"),
            SwaggerResponse(statusCode = HttpStatus.BAD_REQUEST, description = "The request was badly formed", schema = "errors/validationError.json")
    ))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable("id") @SwaggerSummary("The ID of the user to delete") id: String) {

    }
}