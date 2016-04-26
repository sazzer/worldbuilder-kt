package uk.co.grahamcox.worldbuilder.service.dao

import uk.co.grahamcox.worldbuilder.service.Id
import uk.co.grahamcox.worldbuilder.service.Identity

/**
 * Base class for all standard DAOs
 * @param <ID> The type to use for IDs
 * @param <MODEL> The type to use for Models
 */
interface BaseDao<ID : Id, MODEL> {
    /**
     * Get a single record by ID
     * @param id The ID of the Record
     * @return the record
     * @throws ResourceNotFoundException if the record was not found
     */
    fun getById(id: ID) : MODEL

    /**
     * Save the given record
     * @param record The record to save.
     * If the record has an Identity then it will update this record in the data store.
     * If the record does not have an Identity then it will create a new record in the data store
     * @return the record as it has been saved
     * @throws ResourceNotFoundException if the record was not found
     * @throws StaleResourceException if the record was stale
     */
    fun save(record: MODEL) : MODEL

    /**
     * Delete the given record
     * @param identity The record to delete
     * @throws ResourceNotFoundException if the record was not found
     * @throws StaleResourceException if the record was stale
     */
    fun delete(identity: Identity<ID>)
}