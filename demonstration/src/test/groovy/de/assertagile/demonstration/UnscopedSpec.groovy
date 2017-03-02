package de.assertagile.demonstration

import spock.lang.Specification

class UnscopedSpec extends Specification {

    void "this generally important"() {
        expect:
        true
    }

    void "this is generally important too"() {
        expect:
        true
    }
}
