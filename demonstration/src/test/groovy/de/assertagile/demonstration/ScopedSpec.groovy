package de.assertagile.demonstration

import de.assertagile.spockframework.extensions.Scope
import spock.lang.Specification

import static de.assertagile.demonstration.TestScopes.*


@Scope([FEATURE_A, FEATURE_B])
class ScopedSpec extends Specification {

    @Scope(FEATURE_A)
    def "this is important for feature a"() {
        expect:
        true
    }

    @Scope(FEATURE_B)
    def "this is important for feature b"() {
        expect:
        true
    }

    def "this is important for any scope, but it might be ignored due to the Specification's scope"() {
        expect:
        true
    }
}