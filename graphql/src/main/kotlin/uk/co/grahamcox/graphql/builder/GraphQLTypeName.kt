package uk.co.grahamcox.graphql.builder

import org.slf4j.LoggerFactory

/**
 * Representation of a GraphQL Type Name
 * @param typename The raw typename
 * @param nonNullable True if the overall typename is non-nullable
 * @param list True if this is a list of the raw type
 * @param nonNullableList True if this is a list of non-nullable types
 */
data class GraphQLTypeName(val typename: String,
                           val nonNullable: Boolean,
                           val list: Boolean,
                           val nonNullableList: Boolean) {
    companion object{
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(GraphQLTypeName::class.java)

        /** Regex for a typename */
        private val TYPENAME = "[_A-Za-z][_0-9A-Za-z]*"

        /** Regex for a nullable type, with named groups to capture with */
        private val NULLABLE_TYPE = "(?<typename>${TYPENAME})(?<nullable>!)?"

        /** Regex for a list type */
        private val LIST_REGEX = "^\\[${NULLABLE_TYPE}](?<nullablelist>!)?$".toPattern()

        /** Regex for a non-list type */
        private val TYPE_REGEX = "^${NULLABLE_TYPE}$".toPattern()

        /**
         * Parse a given input string to produce the type name
         * @param type The input typename to parse
         * @return the parsed type name
         */
        fun parse(type: String) : GraphQLTypeName {
            val listMatch = LIST_REGEX.matcher(type)

            val result = if (listMatch.matches()) {
                val typename = listMatch.group("typename")
                val list = true
                val nonNullable = listMatch.group("nullablelist") != null
                val nonNullableList = listMatch.group("nullable") != null

                GraphQLTypeName(typename, nonNullable, list, nonNullableList)
            } else {
                val nonListMatch = TYPE_REGEX.matcher(type)
                if (nonListMatch.matches()) {
                    val typename = nonListMatch.group("typename")
                    val list = false
                    val nonNullable = nonListMatch.group("nullable") != null
                    val nonNullableList = false

                    GraphQLTypeName(typename, nonNullable, list, nonNullableList)
                } else {
                    throw IllegalArgumentException("Provided string is not a valid GraphQL Type description")
                }
            }

            LOG.debug("Parsed input {} into type {}", type, result)
            return result
        }
    }
}
