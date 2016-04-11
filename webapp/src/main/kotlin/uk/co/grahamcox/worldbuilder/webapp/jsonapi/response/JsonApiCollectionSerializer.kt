package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

/**
 * Implementation of the JSON API Serializer
 * @param <Input> the Input type to serialize
 * @property type The type to use in the serialized resource
 * @property idGenerator The mechanism to extract the ID from the input data
 * @property attributeGenerator The mechanisms to extract the attributes from the input data
 * @property collectionSelfLinkGenerator The mechanism to generate the Self Link to the collection
 * @property resourceSelfLinkGenerator The mechanism to generate the Self Link to a Resource
 * @property relatedResources The details of the related resources
 * @property resourceListGenerator The means to convert the input object into a list of resources
 */
class JsonApiCollectionSerializer<Input, Resource>(private val type: String,
                                                   private val idGenerator: (Resource) -> Any,
                                                   private val attributeGenerator: Map<String, (Resource) -> Any>,
                                                   private val collectionSelfLinkGenerator: (Input) -> String,
                                                   private val resourceSelfLinkGenerator: (Resource) -> String,
                                                   private val relatedResources: Map<String, JsonApiRelatedResourceSchema<Resource, *>>,
                                                   private val resourceListGenerator: (Input) -> List<Resource>) :
        JsonApiSerializerBase<Resource>() {

    /**
     * Serialize the given Input object to produce a JSON API Response representing the same data
     * @param input The input object to serialize
     * @return The JSON API response for the data
     */
    fun serialize(input: Input): JsonApiResponse<List<JsonApiResource>> {
        val alreadyIncluded = mutableMapOf<String, MutableSet<Any>>()
        val included = mutableListOf<JsonApiResource>()
        val data = mutableListOf<JsonApiResource>()

        val allResources = resourceListGenerator(input)

        allResources.forEach { resource ->
            val relatedResources = buildRelatedResources(resource, relatedResources, alreadyIncluded)
            included.addAll(relatedResources.included)
            data.add(JsonApiResource(
                    type = type,
                    id = idGenerator(resource),
                    attributes = attributeGenerator.mapValues { it.value(resource) },
                    relationships = relatedResources.relationships,
                    links = JsonApiResourceLinks(
                            self = resourceSelfLinkGenerator(resource)
                    )
            ))
        }


        return JsonApiResponse(
                links = JsonApiTopLevelLinks(
                        self = collectionSelfLinkGenerator(input)
                ),
                data = data,
                included = included
        )
    }
}