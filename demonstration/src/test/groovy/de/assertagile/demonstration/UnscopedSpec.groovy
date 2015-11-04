package de.assertagile.demonstration

import spock.lang.Specification

class UnscopedSpec extends Specification {

    def "this generally important"() {
        expect:
        false
    }

    def "this is generally important too"() {
        expect:
        false
    }
}
