package uk.co.grahamcox.worldbuilder.service

import java.time.Instant

/**
 * Representation of the identity of a resource
 * @property id The actual ID
 * @property created When the resource was created
 * @property updated When the resource was last updated
 */
data class Identity<T : Id>(val id: T,
                            val created: Instant,
                            val updated: Instant)