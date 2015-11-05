package de.assertagile.demonstration
import de.assertagile.spockframework.extensions.Scope
import spock.lang.Specification

@Scope([SpecScopes.FeatureA, SpecScopes.FeatureB])
class ScopedSpec extends Specification {

    def "this is important for feature a and b"() {
        expect:
        true
    }

    def "this is important for feature a"() {
        expect:
        false
    }
}
