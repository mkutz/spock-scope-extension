package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject

class ScopeExtensionSpec extends Specification {

    String originalScopes = System.getProperty("spock.scopes", "")

    ConfigObject configMock = Mock()

    @Subject
    ScopeExtension scopeExtension = new ScopeExtension(config: configMock)

    def "if there are no currentScopes all should be executed"() {
        given:
        SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }
        Scope scope = Mock()

        when:
        scopeExtension.visitSpecAnnotation(scope, specInfoMock)

        then:
        0 * specInfoMock.setSkipped(true)
    }

    def "if the spec does not have any scope and there are current scopes set, it should be ignored"() {
        given:
        setCurrentScopes("a,b")

        SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }
        Scope scope = Mock() {
            value() >> null
        }

        when:
        scopeExtension.visitSpecAnnotation(scope, specInfoMock)

        then:
        1 * specInfoMock.setSkipped(true)
    }

    def "if the spec does have a scope which is not in the current scope, it should be ignored"() {
        given:
        setCurrentScopes("a,b")

        SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }
        Scope scope = Mock() {
            value() >> ["d"]
        }

        when:
        scopeExtension.visitSpecAnnotation(scope, specInfoMock)

        then:
        1 * specInfoMock.setSkipped(true)
    }

    def "if the spec does have a scope which is in the current scope, it should not be ignored"() {
        given:
        setCurrentScopes("a,b")

        SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }
        Scope scope = Mock() {
            value() >> ["a"]
        }

        when:
        scopeExtension.visitSpecAnnotation(scope, specInfoMock)

        then:
        0 * specInfoMock.setSkipped(true)
    }

    def cleanup() {
        resetCurrentScopes()
    }

    private void setCurrentScopes(String newScopes) {
        System.setProperty("spock.scopes", newScopes)
    }

    private void resetCurrentScopes() {
        System.setProperty("spock.scopes", originalScopes)
    }
}
