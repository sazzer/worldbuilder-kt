package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

/**
 * Implementation of the JSON API Serializer
 * @param <Input> the Input type to serialize
 * @param type The type to use in the serialized resource
 * @param idGenerator The mechanism to extract the ID from the input data
 * @param attributeGenerator The mechanisms to extract the attributes from the input data
 * @param selfLinkGenerator The mechanism to generate the Self Link to the resource
 */
class JsonApiSerializerImpl<Input>(private val type: String,
                                   private val idGenerator: (Input) -> Any,
                                   private val attributeGenerator: Map<String, (Input) -> Any>,
                                   private val selfLinkGenerator: (Input) -> String) : Serializer<Input> {
    /**
     * Serialize the given Input object to produce a JSON API Response representing the same data
     * @param input The input object to serialize
     * @return The JSON API response for the data
     */
    override fun serialize(input: Input): JsonApiResponse<JsonApiResource> {
        return JsonApiResponse(
                links = JsonApiTopLevelLinks(
                        self = selfLinkGenerator(input)
                ),
                data = JsonApiResource(
                        type = type,
                        id = idGenerator(input),
                        attributes = attributeGenerator.mapValues { it.value(input) }
                ),
                included = null
        )
    }
}