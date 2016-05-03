package uk.co.grahamcox.worldbuilder.webapp.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment

/**
 * Builder to use to build Fields on GraphQL Object types
 */
class GraphQLFieldBuilder : GraphQLBuilderBase<GraphQLFieldBuilder>() {
    /** The type of the field */
    var type: GraphQLTypeName? = null
        private set

    /** The data fetcher for the field */
    var fetcher: DataFetcher? = null
        private set

    /** The reason the field was deprecated, if any */
    var deprecationReason: String? = null
        private set

    /** The arguments of the field */
    val arguments = mutableMapOf<String, GraphQLArgumentBuilder>()

    /**
     * Specify the type of the field
     * @param type The type fo the field
     * @return this, for chaining
     */
    fun withType(type: String) : GraphQLFieldBuilder {
        this.type = GraphQLTypeName.parse(type)
        return this
    }

    /**
     * Specify the data fetcher to use for this field, if any
     * @param fetcher The data fetcher to use
     * @return this, for chaining
     */
    fun withFetcher(fetcher: DataFetcher) : GraphQLFieldBuilder {
        this.fetcher = fetcher
        return this
    }

    /**
     * Specify that the field returns a single static value
     * @param value The value the field should return
     * @return this, for chaining
     */
    fun withValue(value: Any?) = withFetcher(object : DataFetcher {
        override fun get(environment: DataFetchingEnvironment?) = value
    })

    /**
     * Specify the reason the field has been deprecated
     * @param reason The reason the field was deprecated
     * @return this, for chaining
     */
    fun withDeprecationReason(reason: String) : GraphQLFieldBuilder {
        this.deprecationReason = reason
        return this
    }

    /**
     * Add an argument to the field.
     * If the argument with the given name already exists then return it so that it can be further configured
     * @param name The name of the argument
     * @return the argument to configure
     */
    fun withArgument(name: String) = arguments.getOrPut(name) { GraphQLArgumentBuilder() }
}