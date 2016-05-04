package uk.co.grahamcox.worldbuilder.webapp.graphql

import graphql.Scalars
import graphql.schema.*
import org.slf4j.LoggerFactory

/**
 * Mechanism to built a schema from a given registry of schema entries
 * @property registry The registry to work with
 */
class GraphQLSchemaBuilder(private val registry: GraphQLRegistrar) {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(GraphQLSchemaBuilder::class.java)

        val STATIC_TYPE_NAMES = mapOf(
                "string" to Scalars.GraphQLString,
                "integer" to Scalars.GraphQLInt,
                "int" to Scalars.GraphQLInt,
                "long" to Scalars.GraphQLLong,
                "float" to Scalars.GraphQLFloat,
                "boolean" to Scalars.GraphQLBoolean,
                "bool" to Scalars.GraphQLBoolean,
                "id" to Scalars.GraphQLID
        )
    }
    /** The list of type names that we've already built */
    private val builtNames = mutableListOf<String>()

    /**
     * Build the GraphQL Schema from everything registered in the given registry
     * @param registry The registry to build the schema from
     * @return the schema
     */
    fun buildGraphQLSchema() : GraphQLSchema {
        val queryType = GraphQLObjectType.newObject()
                .name("RootQuery")

        registry.queryBuilders.forEach { e ->
            queryType.field(build(e.key, e.value))
        }

        return GraphQLSchema.newSchema()
                .query(queryType.build())
                .build()
    }

    /**
     * Build a new Field definition
     * @param name The name of the field
     * @param builder The field builder to use
     */
    private fun build(name: String,
              builder: GraphQLFieldBuilder) : GraphQLFieldDefinition {
        LOG.debug("Building GraphQL Field for name {}", name)
        val fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
                .name(name)
                .description(builder.description)
                .deprecate(builder.deprecationReason)
                .type(builder.type?.let { buildOutputType(it) })
                .dataFetcher(builder.fetcher)

        builder.arguments.forEach {
            fieldBuilder.argument(build(it.key, it.value))
        }

        LOG.debug("Built GraphQL Field for name {}", name)
        return fieldBuilder.build()
    }

    /**
     * Build a new Argument definition
     * @param name The name of the argument
     * @param builder The argument builder to use
     */
    private fun build(name: String,
                      builder: GraphQLArgumentBuilder) : GraphQLArgument {
        LOG.debug("Building GraphQL Argument for name {}", name)
        val fieldBuilder = GraphQLArgument.newArgument()
                .name(name)
                .description(builder.description)
                .defaultValue(builder.defaultValue)
                .type(builder.type?.let { buildInputType(it) })

        LOG.debug("Built GraphQL Argument for name {}", name)
        return fieldBuilder.build()
    }

    /**
     * Build a new Field definition
     * @param name The name of the field
     * @param builder The field builder to use
     */
    private fun build(name: String,
                      builder: GraphQLEnumBuilder) : GraphQLEnumType {
        LOG.debug("Building GraphQL Enum for name {}", name)
        val enumBuilder = GraphQLEnumType.newEnum()
                .name(name)
                .description(builder.description)

        builder.members.forEach { enumBuilder.value(it) }

        LOG.debug("Built GraphQL Enum for name {}", name)
        return enumBuilder.build()
    }

    /**
     * Build a new Field definition
     * @param name The name of the field
     * @param builder The field builder to use
     */
    private fun build(name: String,
                      builder: GraphQLObjectBuilder) : GraphQLObjectType {
        LOG.debug("Building GraphQL Object for name {}", name)
        val objectBuilder = GraphQLObjectType.newObject()
                .name(name)
                .description(builder.description)

        builder.fields.forEach {
            objectBuilder.field(build(it.key, it.value))
        }

        LOG.debug("Built GraphQL Object for name {}", name)
        return objectBuilder.build()
    }

    /**
     * Build the output type definition for the given typename
     * If the typename represents an internal type then it simply uses that
     * If the typename represents a type we've previously seen then a link to that is built
     * Otherwise the actual type definition is built
     * Additionally this takes care of the NotNull and List wrappers
     * @param type The type to build
     * @return the built type
     */
    private fun buildOutputType(type: GraphQLTypeName) : GraphQLOutputType {
        LOG.debug("Building GraphQL Type for type name {}", type)
        val rawType: GraphQLOutputType = STATIC_TYPE_NAMES[type.typename] ?:
            if (builtNames.contains(type.typename)) {
                GraphQLTypeReference(type.typename)
            } else {
                val builder = registry.getTypeBuilder(type.typename)
                when (builder) {
                    null -> throw IllegalArgumentException("Requested an unknown type name: ${type.typename}")
                    is GraphQLEnumBuilder -> {
                        builtNames.add(type.typename)
                        build(type.typename, builder)
                    }
                    is GraphQLObjectBuilder -> {
                        builtNames.add(type.typename)
                        build(type.typename, builder)
                    }
                    else -> throw IllegalArgumentException("Request to build unsupported type ${builder.javaClass}")
                }
            } as GraphQLOutputType

        val listWrapper = when (type.list) {
            true -> when (type.nonNullableList) {
                true -> GraphQLList(GraphQLNonNull(rawType))
                false -> GraphQLList(rawType)
            }
            false -> rawType
        }

        val result = when (type.nonNullable) {
            true -> GraphQLNonNull(listWrapper)
            false -> listWrapper
        }

        LOG.debug("Built GraphQL Type for type name {}: {}", type, result)
        return result
    }


    /**
     * Build the input type definition for the given typename
     * If the typename represents an internal type then it simply uses that
     * If the typename represents a type we've previously seen then a link to that is built
     * Otherwise the actual type definition is built
     * Additionally this takes care of the NotNull and List wrappers
     * @param type The type to build
     * @return the built type
     */
    private fun buildInputType(type: GraphQLTypeName) : GraphQLInputType {
        LOG.debug("Building GraphQL Type for type name {}", type)
        val rawType: GraphQLInputType = STATIC_TYPE_NAMES[type.typename] ?:
                if (builtNames.contains(type.typename)) {
                    GraphQLTypeReference(type.typename)
                } else {
                    val builder = registry.getTypeBuilder(type.typename)
                    when (builder) {
                        null -> throw IllegalArgumentException("Requested an unknown type name: ${type.typename}")
                        is GraphQLEnumBuilder -> {
                            builtNames.add(type.typename)
                            build(type.typename, builder)
                        }
                        is GraphQLObjectBuilder -> {
                            builtNames.add(type.typename)
                            build(type.typename, builder)
                        }
                        else -> throw IllegalArgumentException("Request to build unsupported type ${builder.javaClass}")
                    }
                } as GraphQLInputType

        val listWrapper = when (type.list) {
            true -> when (type.nonNullableList) {
                true -> GraphQLList(GraphQLNonNull(rawType))
                false -> GraphQLList(rawType)
            }
            false -> rawType
        }

        val result = when (type.nonNullable) {
            true -> GraphQLNonNull(listWrapper)
            false -> listWrapper
        }

        LOG.debug("Built GraphQL Type for type name {}: {}", type, result)
        return result
    }
}
