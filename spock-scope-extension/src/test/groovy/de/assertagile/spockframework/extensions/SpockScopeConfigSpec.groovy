package de.assertagile.spockframework.extensions

import de.assertagile.spockframework.extensions.SpecScopes.A
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification

@Scope(A)
class SpockScopeConfigSpec extends Specification {

    def "config should be read from SpockScopeConfig.groovy if not mocked"() {
        given:
        ScopeExtension scopeExtension = new ScopeExtension()

        SpecInfo scopeASpecInfoMock = Mock() { getName() >> "MockedASpec" }
        Scope scopeA = Mock() { value() >> [SpecScopes.A] }
        SpecInfo scopeBSpecInfoMock = Mock() { getName() >> "MockedBSpec" }
        Scope scopeB = Mock() { value() >> [SpecScopes.B] }

        when:
        scopeExtension.visitSpecAnnotation(scopeA, scopeASpecInfoMock)
        scopeExtension.visitSpecAnnotation(scopeB, scopeBSpecInfoMock)

        then:
        0 * scopeASpecInfoMock.setSkipped(true)
        1 * scopeBSpecInfoMock.setSkipped(true)
    }
}
