package de.assertagile.spockframework.extensions.demonstration

import de.assertagile.spockframework.extensions.Scope
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Title


@Scope(["a", "b"])
class AnotherScopedSpec extends Specification {

    def "this is a scoped tested feature description"() {
        expect:
        true
    }
}