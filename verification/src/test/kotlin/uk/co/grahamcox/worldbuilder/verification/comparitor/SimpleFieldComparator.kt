package uk.co.grahamcox.worldbuilder.verification.comparitor

import org.slf4j.LoggerFactory
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Simple implementation of the Field Comparator that tries to do some simple conversions if necessary
 */
class SimpleFieldComparator : FieldComparator {
    companion object {
        /** The logger to use */
        private val LOG = LoggerFactory.getLogger(SimpleFieldComparator::class.java)
    }

    /**
     * If needed, convert the expected value to the same type as the field value and then compare the two
     */
    override fun compare(expected: String, value: Any): Boolean {
        val valueType = value.javaClass

        val target = if (value is String) {
            expected
        } else {
            val valueOfMethod = findValueOf(valueType)
            if (valueOfMethod != null) {
                valueOfMethod.invoke(null, expected)
            } else {
                val constructor = findStringConstructor(valueType)
                if (constructor != null) {
                    constructor.newInstance(expected)
                } else {
                    throw IllegalArgumentException("Unable to trivially convert the input String to ${valueType}")
                }
            }
        }

        LOG.debug("Converted input string '{}' to '{}' of type {}", expected, target, valueType)
        val result = target.equals(target)
        LOG.debug("Comparison between expected='{}', actual='{}' is {}", expected, value, result)
        return result
    }

    /**
     * Try and find a Public Static method on the class that:
     * * Is called "valueOf"
     * * Takes a single String as a parameter
     * * Returns the correct type
     * @param valueType The class to look over
     * @return the method if found, or null if not
     */
    private fun findValueOf(valueType: Class<*>): Method? {
        val result = valueType.methods.filter { it.name == "valueOf" }
                .filter { Modifier.isStatic(it.modifiers) }
                .filter { it.parameterCount == 1 }
                .filter { it.parameterTypes[0] == String::class.java}
                .filter { it.returnType.equals(valueType) }
                .firstOrNull()

        LOG.debug("Value Of method for class {}: {}", valueType, result)
        return result
    }

    /**
     * Try to find a Public Constructor on the class that takes a single String
     * @param valueType The class to look over
     * @return the constructor if found, or null if not
     */
    private fun findStringConstructor(valueType: Class<*>): Constructor<*>? {
        val result = valueType.constructors.filter { it.parameterCount == 1 }
                .filter { it.parameterCount == 1 }
                .filter { it.parameterTypes[0] == String::class.java}
                .firstOrNull()

        LOG.debug("Single String constructor for class {}: {}", valueType, result)
        return result
    }
}