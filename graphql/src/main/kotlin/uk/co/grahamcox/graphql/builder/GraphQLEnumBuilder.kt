package uk.co.grahamcox.graphql.builder

import kotlin.reflect.KClass

/**
 * Builder to use to build GraphQL Enum types
 */
class GraphQLEnumBuilder : GraphQLBuilderBase<GraphQLEnumBuilder>() {
    /** The members of the enum */
    val members = mutableMapOf<String, Any>()

    /**
     * Add a new member to the enum
     * @param member The member to add
     * @return this, for chaining
     */
    fun withMember(member: String) = withMember(member, member)

    /**
     * Add a new member to the enum
     * @param member The member to add
     * @param value The value to use for the member
     * @return this, for chaining
     */
    fun withMember(member: String, value: Any) : GraphQLEnumBuilder {
        members.put(member, value)
        return this
    }

    /**
     * Add a number of members to the enum
     * @param members The members to add
     * @return this, for chaining
     */
    fun withMembers(vararg members: String) = withMembers(members.toList())

    /**
     * Add a whole collection of members to the enum
     * @param members The members to add
     * @return this, for chaining
     */
    fun withMembers(members: Iterable<String>) : GraphQLEnumBuilder {
        members.forEach { withMember(it) }
        return this
    }

    /**
     * Add all of the keys in a given enumeration class to the enum
     * @param enumClass The class of the enumeration
     * @return this, for chaining
     */
    fun withMembers(enumClass: KClass<*>) =
            enumClass.java.enumConstants
                    .map { it as Enum<*> }
                    .forEach { withMember(it.name, it) }
}