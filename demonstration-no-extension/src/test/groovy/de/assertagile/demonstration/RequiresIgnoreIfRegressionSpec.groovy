package de.assertagile.demonstration

import spock.lang.IgnoreIf
import spock.lang.Requires
import spock.lang.Specification


class RequiresIgnoreIfRegressionSpec extends Specification {

    @FeatureB
    @Requires({ true })
    void "this is important for feature b and requires something"() {
        expect:
        true
    }

    @FeatureB
    @IgnoreIf({ false })
    void "this is important for feature b c and will be ignored if something"() {
        expect:
        true
    }
}
