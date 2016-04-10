package uk.co.grahamcox.worldbuilder.webapp.jsonapi.response

/**
 * Mechanism to take an input object and produce a valid JSON API Response from it
 * @param <Input> the type of the input object
 */
interface Serializer<Input> {
    /**
     * Serialize the given Input object to produce a JSON API Response representing the same data
     * @param input The input object to serialize
     * @return The JSON API response for the data
     */
    fun serialize(input: Input) : JsonApiResponse<JsonApiResource>
}