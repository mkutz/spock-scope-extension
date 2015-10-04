package de.assertagile.demonstration

import de.assertagile.spockframework.extensions.Scope
import spock.lang.Specification


@Scope(["feature a", "feature b"])
class ScopedSpec extends Specification {

    @Scope("feature a")
    def "this is important for feature a"() {
        expect:
        true
    }

    @Scope("feature b")
    def "this is important for feature b"() {
        expect:
        true
    }

    def "this is important for any scope, but it might be ignored due to the Specification's scope"() {
        expect:
        true
    }
}