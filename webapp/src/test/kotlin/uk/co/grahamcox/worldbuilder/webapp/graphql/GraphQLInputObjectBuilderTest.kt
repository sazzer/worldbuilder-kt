package uk.co.grahamcox.worldbuilder.webapp.graphql

import org.junit.Assert
import org.junit.Test

/**
 * Unit Tests for the Input Object Builder
 */
class GraphQLInputObjectBuilderTest {
    /**
     * Test setting fields
     */
    @Test
    fun testFields() {
        val builder = GraphQLInputObjectBuilder()
                .withDescription("The Desc")
                .apply {
                    withField("name")
                            .withDescription("The Name")
                    withField("age")
                            .withDescription("The Age")
                }

        Assert.assertEquals("The Desc", builder.description)
        Assert.assertEquals(2, builder.fields.size)
        Assert.assertNotNull(builder.fields["name"])
        Assert.assertEquals("The Name", builder.fields["name"]?.description)
        Assert.assertNotNull(builder.fields["age"])
        Assert.assertEquals("The Age", builder.fields["age"]?.description)
    }
}