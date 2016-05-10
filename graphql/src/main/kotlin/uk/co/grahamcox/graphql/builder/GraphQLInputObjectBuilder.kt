package uk.co.grahamcox.graphql.builder

/**
 * Builder to use to build GraphQL Input Object types
 */
class GraphQLInputObjectBuilder : GraphQLBuilderBase<GraphQLInputObjectBuilder>() {
    /** The list of fields */
    val fields = mutableMapOf<String, GraphQLInputFieldBuilder>()

    /**
     * Add a field to the object.
     * If the field with the given name already exists then return it so that it can be further configured
     * @param name The name of the field
     * @return the field to configure
     */
    fun withField(name: String) = fields.getOrPut(name) { GraphQLInputFieldBuilder() }
}