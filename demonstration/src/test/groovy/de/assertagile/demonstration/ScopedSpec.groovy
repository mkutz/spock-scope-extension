package de.assertagile.demonstration

import de.assertagile.spockframework.extensions.Scope
import spock.lang.Specification

@Scope([SpecScopes.FeatureA, SpecScopes.FeatureB])
class ScopedSpec extends Specification {

    @Scope(SpecScopes.FeatureA)
    def "this is important for feature a"() {
        expect:
        true
    }

    @Scope(SpecScopes.FeatureB)
    def "this is important for feature b"() {
        expect:
        true
    }

    def "this is important for any scope, but it might be ignored due to the Specification's scope"() {
        expect:
        true
    }
}