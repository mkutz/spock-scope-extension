package de.assertagile.demonstration

import de.assertagile.spockframework.extensions.Scope
import spock.lang.Specification

@Scope([SpecScopes.FeatureA, SpecScopes.FeatureB])
class ScopedFeaturesSpec extends Specification {

    def "this is important for feature a"() {
        expect:
        true
    }

    def "this is important for feature b"() {
        expect:
        true
    }

    def "this is important for any scope"() {
        expect:
        true
    }
}