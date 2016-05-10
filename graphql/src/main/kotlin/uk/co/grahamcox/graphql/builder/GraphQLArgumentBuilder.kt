package uk.co.grahamcox.graphql.builder

/**
 * Builder to use to build Arguments on GraphQL Fields
 */
class GraphQLArgumentBuilder : GraphQLBuilderBase<GraphQLArgumentBuilder>() {
    /** The type of the argument */
    var type: GraphQLTypeName? = null
        private set

    /** The default value for the argument, if any */
    var defaultValue: Any? = null
        private set

    /**
     * Specify the type of the field
     * @param type The type fo the field
     * @return this, for chaining
     */
    fun withType(type: String) : GraphQLArgumentBuilder {
        this.type = GraphQLTypeName.parse(type)
        return this
    }

    /**
     * Specify the default value of the argument
     * @param default The default value
     * @return this, for chaining
     */
    fun withDefault(default: String) : GraphQLArgumentBuilder {
        this.defaultValue = default
        return this
    }
}