package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

/**
 * Mechanism by which a related resource can be extracted
 * @property type The type to use in the relationship
 * @property resourceExtractor The mechanism to extract the relationship entity from the input entity
 * @property idGenerator The mechanism to generate the ID of the relationship entity
 * @property relationshipLinkGenerator The mechanism to generate the Self Link to the relationship
 * @property relatedLinkGenerator The mechanism to generate the Related Link to the relationship
 * @property selfLinkGenerator The mechanism to generate the Self Link to the related resource
 * @property attributeGenerator The mechanism to generate the attributes of the related resource
 */
class JsonApiRelatedResourceGenerator<Input, Related>(val type: String,
                                                      val resourceExtractor: (Input) -> Related?,
                                                      val idGenerator: (Related) -> Any,
                                                      val relationshipLinkGenerator: ((Input, Related) -> String?)? = null,
                                                      val relatedLinkGenerator: (Input, Related) -> String,
                                                      val selfLinkGenerator: (Input, Related) -> String,
                                                      val attributeGenerator: Map<String, (Related) -> Any>) {

}

/**
 * Implementation of the JSON API Serializer
 * @param <Input> the Input type to serialize
 * @property type The type to use in the serialized resource
 * @property idGenerator The mechanism to extract the ID from the input data
 * @property attributeGenerator The mechanisms to extract the attributes from the input data
 * @property selfLinkGenerator The mechanism to generate the Self Link to the resource
 * @property relatedResources The details of the related resources
 */
class JsonApiSerializerImpl<Input>(private val type: String,
                                   private val idGenerator: (Input) -> Any,
                                   private val attributeGenerator: Map<String, (Input) -> Any>,
                                   private val selfLinkGenerator: (Input) -> String,
                                   private val relatedResources: Map<String, JsonApiRelatedResourceGenerator<Input, *>>) :
        Serializer<Input> {
    /**
     * Serialize the given Input object to produce a JSON API Response representing the same data
     * @param input The input object to serialize
     * @return The JSON API response for the data
     */
    override fun serialize(input: Input): JsonApiResponse<JsonApiResource> {
        val relationships = mutableMapOf<String, JsonApiRelationship>()
        val included = mutableListOf<JsonApiResource>()

        val alreadyIncluded = mutableMapOf<String, MutableSet<Any>>()

        relatedResources.forEach { entry ->
            val relatedResourceGenerator: JsonApiRelatedResourceGenerator<Input, Any> = entry.value as JsonApiRelatedResourceGenerator<Input, Any>
            val resource = relatedResourceGenerator.resourceExtractor(input)

            if(resource != null) {
                val id = relatedResourceGenerator.idGenerator(resource)
                val type = relatedResourceGenerator.type

                val relationship = JsonApiRelationship(
                        links = JsonApiRelationshipLinks(
                                self = relatedResourceGenerator.relationshipLinkGenerator?.invoke(input, resource),
                                related = relatedResourceGenerator.relatedLinkGenerator(input, resource)
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
                            attributes = relatedResourceGenerator.attributeGenerator.mapValues { it.value(resource) },
                            links = JsonApiResourceLinks(
                                    self = relatedResourceGenerator.selfLinkGenerator(input, resource)
                            )
                    )

                    included.add(includedResource)
                    alreadyIncluded.getOrPut(type) {
                        mutableSetOf<Any>()
                    }.add(id)
                }
            }
        }

        return JsonApiResponse(
                links = JsonApiTopLevelLinks(
                        self = selfLinkGenerator(input)
                ),
                data = JsonApiResource(
                        type = type,
                        id = idGenerator(input),
                        attributes = attributeGenerator.mapValues { it.value(input) },
                        relationships = relationships
                ),
                included = included.toTypedArray()
        )
    }
}