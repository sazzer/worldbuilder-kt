package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

/**
 * Mechanism by which a related resource can be extracted
 * @param type The type to use in the relationship
 * @param resourceExtractor The mechanism to extract the relationship entity from the input entity
 * @param idGenerator The mechanism to generate the ID of the relationship entity
 * @param selfLinkGenerator The mechanism to generate the Self Link to the relationship
 * @param relatedLinkGenerator The mechanism to generate the Related Link to the relationship
 */
class JsonApiRelatedResourceGenerator<Input, Related>(val type: String,
                                                      val resourceExtractor: (Input) -> Related,
                                                      val idGenerator: (Related) -> Any,
                                                      val selfLinkGenerator: ((Input, Related) -> String?)? = null,
                                                      val relatedLinkGenerator: (Input, Related) -> String) {

}

/**
 * Implementation of the JSON API Serializer
 * @param <Input> the Input type to serialize
 * @param type The type to use in the serialized resource
 * @param idGenerator The mechanism to extract the ID from the input data
 * @param attributeGenerator The mechanisms to extract the attributes from the input data
 * @param selfLinkGenerator The mechanism to generate the Self Link to the resource
 * @param relatedResources The details of the related resources
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
        val relationships = if (relatedResources.isNotEmpty()) {
            relatedResources.mapValues { entry ->
                val relatedResourceGenerator: JsonApiRelatedResourceGenerator<Input, Any> = entry.value as JsonApiRelatedResourceGenerator<Input, Any>
                val resource = relatedResourceGenerator.resourceExtractor(input)

                JsonApiRelationship(
                        links = JsonApiRelationshipLinks(
                                self = relatedResourceGenerator.selfLinkGenerator?.invoke(input, resource),
                                related = relatedResourceGenerator.relatedLinkGenerator(input, resource)
                        ),
                        data = JsonApiResourceIdentifier(
                                type = relatedResourceGenerator.type,
                                id = relatedResourceGenerator.idGenerator(resource)
                        )
                )
            }
        } else {
            null
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
                included = null
        )
    }
}