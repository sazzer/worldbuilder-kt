package uk.co.grahamcox.worldbuilder.webapp.spring

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.grahamcox.worldbuilder.webapp.jsonapi.response.JsonApiCollectionSerializer
import uk.co.grahamcox.worldbuilder.webapp.jsonapi.response.JsonApiRelatedResourceSchema
import uk.co.grahamcox.worldbuilder.webapp.jsonapi.response.JsonApiResourceSerializer
import uk.co.grahamcox.worldbuilder.webapp.worlds.User
import uk.co.grahamcox.worldbuilder.webapp.worlds.World
import uk.co.grahamcox.worldbuilder.webapp.worlds.WorldsController

/**
 * Spring Configuration for the Worlds controller
 */
@Configuration
open class WorldsConfig {
    companion object {
        /** The resource type for the worlds resources */
        private val RESOURCE_TYPE = "world"

        /** The generator of the ID of a World */
        private val RESOURCE_ID_GENERATOR = World::id

        /** The attributes of a World */
        private val RESOURCE_ATTRIBUTES = mapOf(
                "name" to World::name,
                "created" to World::created,
                "updated" to World::updated
        )

        /** The Self Link of a World */
        private val RESOURCE_SELF_LINK_GENERATOR = { world: World -> "/api/worlds/${world.id}" }

        /** The relationship representing the owner of a world */
        private val OWNER_RELATIONSHIP_SCHEMA = JsonApiRelatedResourceSchema<World, User>(
                type = "user",
                resourceExtractor = { world -> User(id = 12345, name = "Terry Pratchett") },
                idGenerator = User::id,
                relationshipLinkGenerator = { world, user -> "/api/worlds/${world.id}/relationships/owner" },
                relatedLinkGenerator = { world, user -> "/api/worlds/${world.id}/owner" },
                selfLinkGenerator = { world, user -> "/api/users/${user.id}" },
                attributeGenerator = mapOf(
                        "name" to User::name
                )
        )

        /** The relationship representing who last edited a world */
        private val EDITED_RELATIONSHIP_SCHEMA = JsonApiRelatedResourceSchema<World, User>(
                type = "user",
                resourceExtractor = { world -> User(id = 12345, name = "Terry Pratchett") },
                idGenerator = User::id,
                relationshipLinkGenerator = { world, user -> "/api/worlds/${world.id}/relationships/edited" },
                relatedLinkGenerator = { world, user -> "/api/worlds/${world.id}/edited" },
                selfLinkGenerator = { world, user -> "/api/users/${user.id}" },
                attributeGenerator = mapOf(
                        "name" to User::name
                )
        )

        /** The complete set of relationships for a world */
        private val RESOURCE_RELATIONSHIPS = mapOf(
                "owner" to OWNER_RELATIONSHIP_SCHEMA,
                "edited" to EDITED_RELATIONSHIP_SCHEMA
        )
    }

    /**
     * Construct the serializer to use for individual worlds
     */
    private fun worldResourceSerializer() = JsonApiResourceSerializer(
            type = RESOURCE_TYPE,
            idGenerator = RESOURCE_ID_GENERATOR,
            attributeGenerator = RESOURCE_ATTRIBUTES,
            selfLinkGenerator = RESOURCE_SELF_LINK_GENERATOR,
            relatedResources = RESOURCE_RELATIONSHIPS
    )

    /**
     * Construct the serializer to use for collections of worlds
     */
    private fun worldCollectionSerializer() = JsonApiCollectionSerializer(
            type = RESOURCE_TYPE,
            resourceListGenerator = { worlds: List<World> -> worlds },
            idGenerator = RESOURCE_ID_GENERATOR,
            attributeGenerator = RESOURCE_ATTRIBUTES,
            collectionSelfLinkGenerator = { worlds -> "/api/worlds" },
            resourceSelfLinkGenerator = RESOURCE_SELF_LINK_GENERATOR,
            relatedResources = RESOURCE_RELATIONSHIPS
    )
    /**
     * Build the Worlds Controller
     * @return the controller
     */
    @Bean
    open fun worldsController() = WorldsController(worldResourceSerializer(), worldCollectionSerializer())
}