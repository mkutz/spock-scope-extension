package de.assertagile.demonstration

import spock.lang.Specification

@FeatureA
class ScopedBothSpec extends Specification {

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
}
