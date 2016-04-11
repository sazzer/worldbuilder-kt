package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

abstract class JsonApiSerializerBase<Input> : Serializer<Input> {
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
    protected fun buildRelatedResources(input: Input,
                                        relatedResources: Map<String, JsonApiRelatedResourceSchema<Input, *>>,
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


}