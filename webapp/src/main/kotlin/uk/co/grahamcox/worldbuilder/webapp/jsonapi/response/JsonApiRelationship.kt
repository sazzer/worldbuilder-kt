package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

/**
 * Representation of a Relationship in a JSON API Response
 * @property links The links of the relationship
 * @property data The data of the relationship
 */
class JsonApiRelationship(val links: JsonApiRelationshipLinks,
                          val data: JsonApiResourceIdentifier)