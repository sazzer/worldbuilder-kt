package uk.co.grahamcox.graphql.builder

/**
 * Builder to use to build GraphQL Object types
 */
class GraphQLObjectBuilder : GraphQLBuilderBase<GraphQLObjectBuilder>() {
    /** The list of interfaces */
    val interfaces = mutableSetOf<String>()

    /** The list of fields */
    val fields = mutableMapOf<String, GraphQLFieldBuilder>()

    /**
     * Add an interface to this object
     * @param interfaceName The name of the interface
     * @return this, for chaining
     */
    fun withInterface(interfaceName: String) : GraphQLObjectBuilder {
        interfaces.add(interfaceName)
       return this
    }

    /**
     * Add a number of interfaces from a given collection
     * @param interfaceNames The interface names to add
     * @return this, for chaining
     */
    fun withInterfaces(interfaceNames: Iterable<String>) : GraphQLObjectBuilder {
        interfaceNames.forEach { withInterface(it) }
        return this
    }

    /**
     * Add a number of interfaces
     * @param interfaceNames The interface names to add
     * @return this, for chaining
     */
    fun withInterfaces(vararg interfaceNames: String) : GraphQLObjectBuilder =
            withInterfaces(interfaceNames.toList())

    /**
     * Add a field to the object.
     * If the field with the given name already exists then return it so that it can be further configured
     * @param name The name of the field
     * @return the field to configure
     */
    fun withField(name: String) = fields.getOrPut(name) { GraphQLFieldBuilder() }
}