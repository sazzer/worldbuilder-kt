package uk.co.grahamcox.graphql.builder

/**
 * Builder to use to build Fields on GraphQL Object types
 */
class GraphQLInputFieldBuilder : GraphQLBuilderBase<GraphQLInputFieldBuilder>() {
    /** The type of the field */
    var type: GraphQLTypeName? = null
        private set

    /** The default value for the field, if any */
    var defaultValue: Any? = null
        private set


    /**
     * Specify the type of the field
     * @param type The type fo the field
     * @return this, for chaining
     */
    fun withType(type: String) : GraphQLInputFieldBuilder {
        this.type = GraphQLTypeName.parse(type)
        return this
    }

    /**
     * Specify the default value of the field
     * @param default The default value
     * @return this, for chaining
     */
    fun withDefault(default: String) : GraphQLInputFieldBuilder {
        this.defaultValue = default
        return this
    }

}