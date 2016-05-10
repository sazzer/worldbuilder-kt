package uk.co.grahamcox.graphql.builder

/**
 * Base class for the GraphQL Builders
 */
abstract class GraphQLBuilderBase<T : GraphQLBuilderBase<T>> {
    /** The description of the entity */
    var description: String = ""
        private set

    /**
     * Builder to set the description
     * @param description The description
     * @return This, for chaining
     */
    fun withDescription(description: String) : T {
        this.description = description
        return this as T
    }
}