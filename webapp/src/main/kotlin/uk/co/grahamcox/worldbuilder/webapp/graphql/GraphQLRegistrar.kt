package uk.co.grahamcox.worldbuilder.webapp.graphql

import org.slf4j.LoggerFactory

/**
 * Registrar with which all of the bits of the GraphQL Schema should be registered
 */
class GraphQLRegistrar {
    companion object {
        /** The logger to use */
        val LOG = LoggerFactory.getLogger(GraphQLRegistrar::class.java)
    }
    /** The object builders we've got so far */
    val objectBuilders = mutableMapOf<String, GraphQLObjectBuilder>()
    /** The interface builders we've got so far */
    val interfaceBuilders = mutableMapOf<String, GraphQLObjectBuilder>()
    /** The enum builders we've got so far */
    val enumBuilders = mutableMapOf<String, GraphQLEnumBuilder>()
    /** The query builders we've got so far */
    val queryBuilders = mutableMapOf<String, GraphQLFieldBuilder>()
    /** The mutation builders we've got so far */
    val mutationBuilders = mutableMapOf<String, GraphQLFieldBuilder>()

    /**
     * Check that the given name is not yet used by anything
     * @param name The name to check for
     */
    private fun checkNameUnused(name: String) {
        listOf("object" to objectBuilders,
                "interface" to interfaceBuilders,
                "enum" to enumBuilders).forEach {
            if (it.second.containsKey(name)) {
                throw IllegalArgumentException("The given name is already used as an ${it.first}")
            }
        }
    }
    /**
     * Start creating a new Object
     * @param name The name of the new Object
     * @return the builder to use for the object
     */
    fun newObject(name: String) : GraphQLObjectBuilder {
        checkNameUnused(name)
        LOG.debug("Adding a new object: {}", name)
        return objectBuilders.getOrPut(name) {
            GraphQLObjectBuilder()
        }
    }

    /**
     * Start creating a new Interface
     * @param name The name of the new Interface
     * @return the builder to use for the Interface
     */
    fun newInterface(name: String) : GraphQLObjectBuilder{
        checkNameUnused(name)
        LOG.debug("Adding a new interface: {}", name)
        return interfaceBuilders.getOrPut(name) {
            GraphQLObjectBuilder()
        }
    }

    /**
     * Start creating a new Enumeration
     * @param name The name of the new Enumeration
     * @return the builder to use for the Enumeration
     */
    fun newEnum(name: String) : GraphQLEnumBuilder {
        checkNameUnused(name)
        LOG.debug("Adding a new enumeration: {}", name)
        return enumBuilders.getOrPut(name) {
            GraphQLEnumBuilder()
        }
    }

    /**
     * Start creating a new Query
     * @param name The name of the new Query
     * @return the builder to use for the Query
     */
    fun newQuery(name: String) : GraphQLFieldBuilder {
        LOG.debug("Adding a new query: {}", name)
        return queryBuilders.getOrPut(name) {
            GraphQLFieldBuilder()
        }
    }

    /**
     * Start creating a new Mutation
     * @param name The name of the new Mutation
     * @return the builder to use for the Mutation
     */
    fun newMutation(name: String) : GraphQLFieldBuilder {
        LOG.debug("Adding a new mutation: {}", name)
        return mutationBuilders.getOrPut(name) {
            GraphQLFieldBuilder()
        }
    }

    /**
     * Get the builder for the type with the given name.
     * This will return one of the Object, Enum or Interface builders depending on what the name is registered as.
     * It might also return null if the name isn't registered
     * @param name The name to get
     * @return the builder for this name, if there is one
     */
    fun getTypeBuilder(name: String) : GraphQLBuilderBase<*>? {
        return objectBuilders[name]
            ?: enumBuilders[name]
            ?: interfaceBuilders[name]
    }
}