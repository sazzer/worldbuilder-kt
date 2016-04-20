package uk.co.grahamcox.worldbuilder.service

/**
 * Exception to indicate that a resource couldn't be found
 * @property id The ID of the resource that couldn't be found
 */
class ResourceNotFoundException(val id: Id) : Exception("No resource with the ID ${id} could be found")