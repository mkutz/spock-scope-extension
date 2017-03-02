package de.assertagile.demonstration

import de.assertagile.spockframework.extensions.Scope
import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Specification


class RequiresIgnoreIfRegressionSpec extends Specification {

    @Scope([SpecScopes.FeatureB])
    @Requires({ true })
    void "this is important for feature b and requires something"() {
        expect:
        false
    }

    @Scope([SpecScopes.FeatureB])
    @IgnoreIf({ false })
    void "this is important for feature b c and will be ignored if something"() {
        expect:
        false
    }
}
