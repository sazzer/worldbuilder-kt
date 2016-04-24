package uk.co.grahamcox.worldbuilder.dao.embedded.transformers

/**
 * Interface describing a transformer between types
 */
interface Transformer {
    /**
     * Actually transform the input value to an output value
     * @param input The input value to transform
     * @return the transformed value
     */
    fun transform(input: Any?): Any?
}