package uk.co.grahamcox.worldbuilder.webapp.graphql

import kotlin.reflect.KClass

/**
 * Builder to use to build GraphQL Unions
 */
class GraphQLUnionBuilder : GraphQLBuilderBase<GraphQLUnionBuilder>() {
    /** Map of the types that this union supports */
    val unionTypes = mutableMapOf<String, (Any) -> Boolean>()

    /**
     * Add a new type to the union, with the given function to identify type objects
     * @param typeName The name of the type in the union
     * @param identifier The function to use to identify this type of member
     * @return this, for chaining
     */
    fun withType(typeName: String, identifier: (Any) -> Boolean) : GraphQLUnionBuilder {
        unionTypes[typeName] = identifier
        return this
    }

    /**
     * Add a new type to the union, which matches a given type for it's object definitions
     * @param typeName The name of the type in the union
     * @param typeClass The class to use for the union model class
     * @return this, for chaining
     */
    fun withType(typeName: String, typeClass: KClass<*>) = withType(typeName) {
        it -> typeClass.java.isInstance(it)
    }
}