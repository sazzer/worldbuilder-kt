package uk.co.grahamcox.worldbuilder.webapp.swagger.model

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * The representation of the various operations available on a path
 * @property get The operation when you GET the path
 * @property put The operation when you PUT the path
 * @property post The operation when you POST the path
 * @property delete The operation when you DELETE the path
 * @property options The operation when you OPTIONS the path
 * @property head The operation when you HEAD the path
 * @property patch The operation when you PATCH the path
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class Path(val get: Operation? = null,
                val put: Operation? = null,
                val post: Operation? = null,
                val delete: Operation? = null,
                val options: Operation? = null,
                val head: Operation? = null,
                val patch: Operation? = null)