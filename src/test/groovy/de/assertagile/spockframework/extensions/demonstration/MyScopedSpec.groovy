package de.assertagile.spockframework.extensions.demonstration

import de.assertagile.spockframework.extensions.Scope
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Title


@Scope(["a", "b"])
class MyScopedSpec extends Specification {

    @Scope("a")
    def "this is a manual tested feature description"() {
        expect:
        true
    }

    @Scope("b")
    def "this is a manual tested feature description with additional information"() {
        expect:
        false
    }
}