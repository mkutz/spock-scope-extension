package de.assertagile.demonstration

import de.assertagile.spockframework.extensions.Scope
import spock.lang.Specification

@Scope(SpecScopes.FeatureA)
class ScopedBothSpec extends Specification {

    @Scope(SpecScopes.FeatureA)
    void "this is important for feature a"() {
        expect:
        true
    }

    @Scope([SpecScopes.FeatureB, SpecScopes.FeatureC])
    void "this is important for feature b and c"() {
        expect:
        true
    }
}
