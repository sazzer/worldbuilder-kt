package uk.co.grahamcox.worldbuilder.service

import com.fasterxml.jackson.annotation.JsonValue
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import org.apache.commons.lang.builder.ToStringStyle

/**
 * Interface describing an ID
 */
abstract class Id(@get:JsonValue val id: String) {
    override fun equals(other: Any?) = EqualsBuilder.reflectionEquals(this, other)

    override fun hashCode() = HashCodeBuilder.reflectionHashCode(this)

    override fun toString() = ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE)
}