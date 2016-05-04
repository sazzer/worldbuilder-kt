package uk.co.grahamcox.worldbuilder.webapp.graphql

/**
 * Handler to register GraphQL Schema components
 */
interface GraphQLRegistration {
    /**
     * Register everything we want to with the provided GraphQL Registrar
     * @param registrar The registrar to register with
     */
    fun register(registrar: GraphQLRegistrar)
}