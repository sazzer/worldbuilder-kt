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
class JsonApiRelatedResourceSchema<Input, Related>(val type: String,
                                                   val resourceExtractor: (Input) -> Related?,
                                                   val idGenerator: (Related) -> Any,
                                                   val relationshipLinkGenerator: ((Input, Related) -> String?)? = null,
                                                   val relatedLinkGenerator: (Input, Related) -> String,
                                                   val selfLinkGenerator: (Input, Related) -> String,
                                                   val attributeGenerator: Map<String, (Related) -> Any>)