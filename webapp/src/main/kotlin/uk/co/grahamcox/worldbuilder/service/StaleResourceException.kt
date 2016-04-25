package uk.co.grahamcox.worldbuilder.service

/**
 * Exception to indicate that a resource has been modified in the data store more recently than expected
 * @property id The ID of the resource that was modified
 */
class StaleResourceException(val id: Id) : Exception("No resource with the ID ${id} was stale")