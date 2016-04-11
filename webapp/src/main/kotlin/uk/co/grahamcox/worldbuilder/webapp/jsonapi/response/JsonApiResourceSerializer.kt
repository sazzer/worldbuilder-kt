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
        JsonApiSerializerBase<Input>() {

    /**
     * Serialize the given Input object to produce a JSON API Response representing the same data
     * @param input The input object to serialize
     * @return The JSON API response for the data
     */
    override fun serialize(input: Input): JsonApiResponse<JsonApiResource> {
        val alreadyIncluded = mutableMapOf<String, MutableSet<Any>>()

        val relatedResources = buildRelatedResources(input, relatedResources, alreadyIncluded)

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