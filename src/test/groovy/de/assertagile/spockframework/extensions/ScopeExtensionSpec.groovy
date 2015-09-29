package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject

class ScopeExtensionSpec extends Specification {

    ConfigObject configMock = Mock()

    @Subject
    ScopeExtension scopeExtension = new ScopeExtension(config: configMock)

    def "if there are no currentScopes all should be executed"() {
        given:
        configMock.get("scopes") >> null
        configMock.get("currentScopes") >> null

        SpecInfo specInfoMock = Mock()
        Scope scope = Mock()

        when:
        scopeExtension.visitSpecAnnotation(scope, specInfoMock)

        then:
        specInfoMock.setSkipped(false)
    }

    def "if the spec does not have any scope and there are current scopes set, it should be ignored"() {
        given:
        configMock.get("scopes") >> ["a", "b", "c", "d"]
        configMock.get("currentScopes") >> ["a", "b"]

        SpecInfo specInfoMock = Mock()
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
        configMock.get("scopes") >> ["a", "b", "c", "d"]
        configMock.get("currentScopes") >> ["a", "b"]

        SpecInfo specInfoMock = Mock()
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
        configMock.get("scopes") >> ["a", "b", "c", "d"]
        configMock.get("currentScopes") >> ["a", "b"]

        SpecInfo specInfoMock = Mock()
        Scope scope = Mock() {
            value() >> ["a"]
        }

        when:
        scopeExtension.visitSpecAnnotation(scope, specInfoMock)

        then:
        1 * specInfoMock.setSkipped(false)
    }

    def "if the spec's scope is not configured, an exception is thrown"() {
        given:
        configMock.get("scopes") >> ["a", "b", "c", "d"]
        configMock.get("currentScopes") >> ["a", "b"]

        SpecInfo specInfoMock = Mock()
        Scope scope = Mock() {
            value() >> ["z"]
        }

        when:
        scopeExtension.visitSpecAnnotation(scope, specInfoMock)

        then:
        thrown(Exception)
    }
}
