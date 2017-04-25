package de.assertagile.demonstration

import spock.lang.Specification

@FeatureA
@FeatureB
class ScopedSpec extends Specification {

    void "this is important for feature a and b"() {
        expect:
        true
    }

    void "this is important for feature a"() {
        expect:
        true
    }
}
