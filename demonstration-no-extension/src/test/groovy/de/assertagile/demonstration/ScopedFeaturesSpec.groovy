package de.assertagile.demonstration

import spock.lang.Specification

class ScopedFeaturesSpec extends Specification {

    @FeatureA
    void "this is important for feature a"() {
        expect:
        true
    }

    @FeatureB
    @FeatureC
    void "this is important for feature b and c"() {
        expect:
        true
    }

    void "this is not important for a particular"() {
        expect:
        true
    }
}
