package uk.co.grahamcox.worldbuilder.service.users.dao

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import org.bson.Document
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.Identity
import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.dao.BaseMongoDao
import uk.co.grahamcox.worldbuilder.service.users.User
import uk.co.grahamcox.worldbuilder.service.users.UserId
import java.time.Clock
import java.util.*

/**
 * User DAO that works in terms of MongoDB
 * @param collection The collection to get the data from
 * @param clock The clock to use
 */
class UserMongoDao(collection: MongoCollection<Document>, clock: Clock) :
        UserDao, BaseMongoDao<UserId, User>(collection, clock) {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(UserMongoDao::class.java)
    }

    /**
     * Attempt to find the user that has the given email address
     * @param email The email address to search for
     * @return the user with this email address if found. May be null if one wasn't found
     */
    override fun findByEmail(email: String): User? {
        LOG.debug("Loading the User record with Email address {}", email)

        val record: Document? = collection.find(Filters.eq("email", email))
                .first()

        LOG.debug("Loaded BSON Record {}", record)

        val result = record?.let { parseRecord(it) }

        LOG.debug("Loaded Model object {}", result)
        return result
    }

    /**
     * Load a User object from the given MongoDB Document
     * @param record The record to parse
     * @return the parsed User object
     */
    override fun parseRecord(record: Document): User {
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

    /**
     * Build the details to update in the database
     * @param record The record to update
     * @return the update set
     */
    override fun buildUpdateDetails(record: User) = BasicDBObject(
            mapOf(
                    "\$set" to mapOf(
                            "name" to record.name,
                            "email" to record.email,
                            "enabled" to record.enabled,
                            "verificationCode" to record.verificationCode
                    )
            )
    )

    /**
     * Determne the ID to use for the record when saving it
     * This should be the existing ID if there is one, or a new ID otherwise
     * @param record The record to determine the ID of
     * @return the ID
     */
    override fun determineId(record: User) : UserId = if (record.identity == null) {
        UserId(UUID.randomUUID().toString())
    } else {
        record.identity.id
    }

    /**
     * Get the Identity of the given record
     * @param record The record to get the Identity of
     * @return the identity of the record
     */
    override fun getIdentity(record: User) = record.identity
}