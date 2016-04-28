package uk.co.grahamcox.worldbuilder.webapp

import org.slf4j.LoggerFactory
import uk.co.grahamcox.worldbuilder.service.Identity
import java.security.MessageDigest
import java.util.*

/**
 * Mechanism by which we can generate ETag Strings
 */
object EtagGenerator {
    /** The logger to use */
    private val LOG = LoggerFactory.getLogger(EtagGenerator::class.java)

    /** The string to use to build the unhashed etag */
    private val ETAG_FORMAT_STRING = "%s:%s:%s:%s"

    /**
     * Actually generate an ETag for the given Identity
     * @param identity The Identity to generate the Etag for
     * @return the etag
     */
    fun generateEtag(identity: Identity<*>) : String {
        val inputString = ETAG_FORMAT_STRING.format(
                identity.id.javaClass.simpleName,
                identity.id,
                identity.created,
                identity.updated
        )

        val md = MessageDigest.getInstance("SHA-1")
        md.update(inputString.toByteArray())
        val hashedInputString = md.digest()
        val result = Base64.getEncoder().encodeToString(hashedInputString)
        LOG.debug("Generating ETag for identity {}. Input string {}. Result {}", identity, inputString, result)
        return "\"${result}\""
    }
}