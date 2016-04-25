package uk.co.grahamcox.worldbuilder.service.users.dao

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import com.mongodb.ErrorCategory
import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.Id
import uk.co.grahamcox.worldbuilder.service.Identity
import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.StaleResourceException
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserId
import java.time.Instant
import java.util.*

/**
 * User DAO that works in terms of MongoDB
 * @property collection The collection to get the data from
 */
class UserMongoDao(private val collection: MongoCollection<Document>) : UserDao {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(UserMongoDao::class.java)
    }

    /**
     * Get a single User record by ID
     * @param id The ID of the User
     * @return the User record
     * @throws ResourceNotFoundException if the user was not found
     */
    override fun getById(id: UserId): User {
        LOG.debug("Loading the user with ID {}", id)

        val record = collection.find(Filters.eq("_id", id.id))
            .first()
            ?: throw ResourceNotFoundException(id)

        LOG.debug("Loaded BSON Record {}", record)

        val user = loadUserFromRecord(record)

        LOG.debug("Loaded User {}", user)
        return user
    }

    /**
     * Load a User object from the given MongoDB Document
     * @param record The record to parse
     * @return the parsed User object
     */
    private fun loadUserFromRecord(record: Document): User {
        val user = User(
                identity = Identity(
                        id = UserId(record.getString("_id")),
                        created = record.getDate("created").toInstant(),
                        updated = record.getDate("updated").toInstant()
                ),
                name = record.getString("name"),
                email = record.getString("email"),
                enabled = record.getBoolean("enabled"),
                verificationCode = record.getString("verificationCode")
        )
        return user
    }

    private fun buildUpdateDetails(user: User) = BasicDBObject(
            mapOf(
                    "\$set" to mapOf(
                            "name" to user.name,
                            "email" to user.email,
                            "enabled" to user.enabled,
                            "verificationCode" to user.verificationCode,
                            "updated" to Date.from(Instant.now())
                    ),
                    "\$setOnInsert" to mapOf(
                            "created" to Date.from(Instant.now())
                    )
            )
    )

    private fun getSavedUserId(user: User) : UserId = if (user.identity == null) {
        UserId(UUID.randomUUID().toString())
    } else {
        user.identity.id
    }

    fun determineUpdateFailure(id: Id) {
        val count = collection.count(Filters.eq("_id", id.id))
        if (count == 0L) {
            throw ResourceNotFoundException(id)
        } else {
            throw StaleResourceException(id)
        }

    }
    /**
     * Save the given user
     * @param user The user to save.
     * If the user has an Identity then it will update this user in the data store.
     * If the user does not have an Identity then it will create a new user in the data store
     * @return the user as it has been saved
     * @throws ResourceNotFoundException if the user was not found
     * @throws StaleResourceException if the user was stale
     */
    override fun save(user: User): User {
        val updateDetails = buildUpdateDetails(user)
        val userId = getSavedUserId(user)
        val matchDetails = BasicDBObject("_id", userId.id)
        if (user.identity != null) {
            matchDetails.append("updated", Date.from(user.identity.updated))
        }

        LOG.debug("Updating user matched by {} to have details {}", matchDetails, updateDetails)
        val result = collection.updateOne(matchDetails,
                updateDetails,
                UpdateOptions().upsert(user.identity == null))

        LOG.debug("Updated user matched by {} to have details {} with result {}",
                matchDetails, updateDetails, result)

        if (result.modifiedCount == 0L && result.upsertedId == null) {
            determineUpdateFailure(userId)
        }

        val newUserResult = collection.find(Filters.eq("_id", matchDetails["_id"]))
                .first()
        return loadUserFromRecord(newUserResult)
    }

    /**
     * Delete the given user
     * @param user The user to delete
     * @throws ResourceNotFoundException if the user was not found
     * @throws StaleResourceException if the user was stale
     */
    override fun delete(user: Identity<UserId>) {
        val removed = collection.findOneAndDelete(Filters.and(
                Filters.eq("_id", user.id.id),
                Filters.eq("updated", Date.from(user.updated))
        ))

        if (removed == null) {
            determineUpdateFailure(user.id)
        }
    }
}