package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

/**
 * Implementation of the JSON API Serializer
 * @param <Input> the Input type to serialize
 * @property type The type to use in the serialized resource
 * @property idGenerator The mechanism to extract the ID from the input data
 * @property attributeGenerator The mechanisms to extract the attributes from the input data
 * @property selfLinkGenerator The mechanism to generate the Self Link to the resource
 * @property relatedResources The details of the related resources
 */
class JsonApiResourceSerializer<Input>(private val type: String,
                                       private val idGenerator: (Input) -> Any,
                                       private val attributeGenerator: Map<String, (Input) -> Any>,
                                       private val selfLinkGenerator: (Input) -> String,
                                       private val relatedResources: Map<String, JsonApiRelatedResourceSchema<Input, *>>) :
        Serializer<Input> {

    /**
     * Results of building the relationship details of a resource
     * @property relationships The relationships of the resource
     * @property included The included resources
     */
    data class RelatedResourcesResult(val relationships: Map<String, JsonApiRelationship>,
                                      val included: List<JsonApiResource>)

    /**
     * Build the relationship details of a resource
     * @param input The input object
     * @param alreadyIncluded The details of already included details
     * @return the related resource details
     */
    private fun buildRelatedResources(input: Input,
                                      alreadyIncluded: MutableMap<String, MutableSet<Any>>) : RelatedResourcesResult {
        val relationships = mutableMapOf<String, JsonApiRelationship>()
        val included = mutableListOf<JsonApiResource>()

        relatedResources.forEach { entry ->
            val relatedResourceSchema: JsonApiRelatedResourceSchema<Input, Any> = entry.value as JsonApiRelatedResourceSchema<Input, Any>
            val resource = relatedResourceSchema.resourceExtractor(input)

            if(resource != null) {
                val id = relatedResourceSchema.idGenerator(resource)
                val type = relatedResourceSchema.type

                val relationship = JsonApiRelationship(
                        links = JsonApiRelationshipLinks(
                                self = relatedResourceSchema.relationshipLinkGenerator?.invoke(input, resource),
                                related = relatedResourceSchema.relatedLinkGenerator(input, resource)
                        ),
                        data = JsonApiResourceIdentifier(
                                type = type,
                                id = id
                        )
                )
                relationships.put(entry.key, relationship)

                if (!(alreadyIncluded.get(type)?.contains(id) ?: false)) {
                    val includedResource = JsonApiResource(
                            type = type,
                            id = id,
                            attributes = relatedResourceSchema.attributeGenerator.mapValues { it.value(resource) },
                            links = JsonApiResourceLinks(
                                    self = relatedResourceSchema.selfLinkGenerator(input, resource)
                            )
                    )

                    included.add(includedResource)
                    alreadyIncluded.getOrPut(type) {
                        mutableSetOf<Any>()
                    }.add(id)
                }
            }
        }

        return RelatedResourcesResult(relationships = relationships,
                included = included)
    }

    /**
     * Serialize the given Input object to produce a JSON API Response representing the same data
     * @param input The input object to serialize
     * @return The JSON API response for the data
     */
    override fun serialize(input: Input): JsonApiResponse<JsonApiResource> {
        val alreadyIncluded = mutableMapOf<String, MutableSet<Any>>()

        val relatedResources = buildRelatedResources(input, alreadyIncluded)

        return JsonApiResponse(
                links = JsonApiTopLevelLinks(
                        self = selfLinkGenerator(input)
                ),
                data = JsonApiResource(
                        type = type,
                        id = idGenerator(input),
                        attributes = attributeGenerator.mapValues { it.value(input) },
                        relationships = relatedResources.relationships
                ),
                included = relatedResources.included
        )
    }
}