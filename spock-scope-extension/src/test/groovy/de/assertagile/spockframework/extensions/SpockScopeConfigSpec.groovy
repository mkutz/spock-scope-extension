package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification

import static de.assertagile.spockframework.extensions.Scopes.*


class SpockScopeConfigSpec extends Specification {

    def "config should be read from SpockScopeConfig.groovy if not mocked"() {
        given:
        ScopeExtension scopeExtension = new ScopeExtension()

        SpecInfo scopeASpecInfoMock = Mock() { getName() >> "MockedASpec" }
        Scope scopeA = Mock() { value() >> [FEATURE_A] }
        SpecInfo scopeBSpecInfoMock = Mock() { getName() >> "MockedBSpec" }
        Scope scopeB = Mock() { value() >> [FEATURE_B] }

        when:
        scopeExtension.visitSpecAnnotation(scopeA, scopeASpecInfoMock)
        scopeExtension.visitSpecAnnotation(scopeB, scopeBSpecInfoMock)

        then:
        0 * scopeASpecInfoMock.setSkipped(true)
        1 * scopeBSpecInfoMock.setSkipped(true)
    }
}