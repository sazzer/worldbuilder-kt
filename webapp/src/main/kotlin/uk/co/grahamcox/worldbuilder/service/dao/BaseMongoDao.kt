package uk.co.grahamcox.worldbuilder.service.dao

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.Id
import uk.co.grahamcox.worldbuilder.service.Identity
import uk.co.grahamcox.worldbuilder.service.ResourceNotFoundException
import uk.co.grahamcox.worldbuilder.service.StaleResourceException
import java.util.*

abstract class BaseMongoDao<ID : Id, MODEL>(protected val collection: MongoCollection<Document>) : BaseDao<ID, MODEL> {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(BaseMongoDao::class.java)
    }

    /**
     * Get a single record by ID
     * @param id The ID of the Record
     * @return the record
     * @throws ResourceNotFoundException if the record was not found
     */
    override fun getById(id: ID): MODEL {
        LOG.debug("Loading the record with ID {}", id)

        val record = collection.find(Filters.eq("_id", id.id))
                .first()
                ?: throw ResourceNotFoundException(id)

        LOG.debug("Loaded BSON Record {}", record)

        val result = parseRecord(record)

        LOG.debug("Loaded Model object {}", result)
        return result
    }

    /**
     * Parse the provided MongoDB database record to build a model object
     * @param record The record to parse
     * @return the parsed model object
     */
    protected abstract fun parseRecord(record: Document) : MODEL

    /**
     * Determine if an update failure was caused by the resource being unknown or out of date
     * @param id The ID of the resource
     * @throws ResourceNotFoundException if the ID was not found
     * @throws StaleResourceException if the ID was found, since this means that it has been modified since
     */
    protected fun determineUpdateFailure(id: Id) {
        val count = collection.count(Filters.eq("_id", id.id))
        if (count == 0L) {
            throw ResourceNotFoundException(id)
        } else {
            throw StaleResourceException(id)
        }
    }

    /**
     * Build the details to update in the database
     * @param record The record to update
     * @return the update set
     */
    protected abstract fun buildUpdateDetails(record: MODEL) : BasicDBObject

    /**
     * Get the Identity of the given record
     * @param record The record to get the Identity of
     * @return the identity of the record
     */
    protected abstract fun getIdentity(record: MODEL) : Identity<ID>?

    /**
     * Determne the ID to use for the record when saving it
     * This should be the existing ID if there is one, or a new ID otherwise
     * @param record The record to determine the ID of
     * @return the ID
     */
    protected abstract fun determineId(record: MODEL) : ID

    /**
     * Save the given record
     * @param record The record to save.
     * If the record has an Identity then it will update this record in the data store.
     * If the record does not have an Identity then it will create a new record in the data store
     * @return the record as it has been saved
     * @throws ResourceNotFoundException if the record was not found
     * @throws StaleResourceException if the record was stale
     */
    override fun save(record: MODEL): MODEL {
        val updateDetails = buildUpdateDetails(record)
        val recordId = determineId(record)
        val identity = getIdentity(record)

        val matchDetails = BasicDBObject("_id", recordId.id)
        if (identity != null) {
            matchDetails.append("updated", Date.from(identity.updated))
        }

        LOG.debug("Updating user matched by {} to have details {}", matchDetails, updateDetails)
        val result = collection.updateOne(matchDetails,
                updateDetails,
                UpdateOptions().upsert(identity == null))

        LOG.debug("Updated user matched by {} to have details {} with result {}",
                matchDetails, updateDetails, result)

        if (result.modifiedCount == 0L && result.upsertedId == null) {
            determineUpdateFailure(recordId)
        }

        val newUserResult = collection.find(Filters.eq("_id", matchDetails["_id"]))
                .first()
        return parseRecord(newUserResult)
    }

    /**
     * Delete the given record
     * @param identity The record to delete
     * @throws ResourceNotFoundException if the record was not found
     * @throws StaleResourceException if the record was stale
     */
    override fun delete(identity: Identity<ID>) {
        LOG.debug("Attempting to delete record identified by {}", identity)
        val removed = collection.findOneAndDelete(Filters.and(
                Filters.eq("_id", identity.id.id),
                Filters.eq("updated", Date.from(identity.updated))
        ))

        if (removed == null) {
            LOG.warn("Record identified by {} could not be deleted")
            determineUpdateFailure(identity.id)
        }
    }
}