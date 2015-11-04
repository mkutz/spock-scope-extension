package de.assertagile.spockframework.extensions

import de.assertagile.spockframework.extensions.SpecScopes.A
import de.assertagile.spockframework.extensions.SpecScopes.B
import de.assertagile.spockframework.extensions.SpecScopes.C
import de.assertagile.spockframework.extensions.SpecScopes.D
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static de.assertagile.spockframework.extensions.ScopeExtensionSpec.Skipped.NOT_SKIPPED
import static de.assertagile.spockframework.extensions.ScopeExtensionSpec.Skipped.SKIPPED

class ScopeExtensionSpec extends Specification {

    String originalIncludedScopes = System.getProperty("spock.scopes", "")
    String originalExcludedScopes = System.getProperty("spock.excludedScopes", "")

    ConfigObject configMock = Mock()

    enum Skipped {
        SKIPPED, NOT_SKIPPED

        public boolean asBoolean() { this == SKIPPED }

        public String toString() { super.toString().toLowerCase().replace("_", " ") }
    }

    @Subject
    ScopeExtension scopeExtension = new ScopeExtension(config: configMock)

    FeatureInfo featureInfoMock = Mock() { getName() >> "I am a mocked feature" }
    SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }

    @Unroll("with -Dspock.scopes #includedScopes and -Dspock.excludedScopes #excludedScopes, a feature or spec with scopes #specOrFeatureScopes should be #skipped")
    def "only tests in included scopes should be executed, while tests in excluded scopes should always be skipped"(
        Set<Class<? extends SpecScope>> includedScopes, Set<Class<? extends SpecScope>> excludedScopes, Set<Class<? extends SpecScope>> specOrFeatureScopes, Skipped skipped) {
        given:
        setSystemParameters(includedScopes, excludedScopes)

        and:
        Scope scopeMock = Mock() { value() >> specOrFeatureScopes }

        when:
        scopeExtension.visitSpecAnnotation(scopeMock, specInfoMock)
        scopeExtension.visitFeatureAnnotation(scopeMock, featureInfoMock)

        then:
        (skipped ? 1 : 0) * specInfoMock.setSkipped(true)
        (skipped ? 1 : 0) * featureInfoMock.setSkipped(true)

        where:
        includedScopes | excludedScopes | specOrFeatureScopes || skipped
        []             | []             | []                  || NOT_SKIPPED // execution not scoped
        [A]            | []             | [A]                 || NOT_SKIPPED // in scope
        [A]            | []             | []                  || SKIPPED     // not in scope
        [A]            | [A]            | [A]                 || SKIPPED     // excluded and included
        [A]            | [A]            | []                  || SKIPPED     // empty scope, scoped execution
        [A, B]         | [C, D]         | [A]                 || NOT_SKIPPED // in scope, multiple scopes
        [A, B]         | [C, D]         | [C]                 || SKIPPED     // not in scope, multiple scopes
        [A]            | []             | [A, C]              || NOT_SKIPPED // included, multiple test scopes
        []             | [A]            | [A, C]              || SKIPPED     // excluded, multiple test scopes
    }

    @Unroll("with includedScopes = #includedScopes and excludedScopes = #excludedScopes, a feature or spec with scopes #specOrFeatureScopes should be #skipped")
    def "only tests in included scopes should be executed, while tests in excluded scopes should always be skipped"(
        List<String> includedScopes, List<String> excludedScopes, List<String> specOrFeatureScopes, Skipped skipped) {
        given:
        setConfigParameters(includedScopes, excludedScopes)

        and:
        Scope scopeMock = Mock() { value() >> specOrFeatureScopes }

        when:
        scopeExtension.visitSpecAnnotation(scopeMock, specInfoMock)
        scopeExtension.visitFeatureAnnotation(scopeMock, featureInfoMock)

        then:
        (skipped ? 1 : 0) * specInfoMock.setSkipped(true)
        (skipped ? 1 : 0) * featureInfoMock.setSkipped(true)

        where:
        includedScopes | excludedScopes | specOrFeatureScopes || skipped
        ["A"]          | []             | [A]                 || NOT_SKIPPED // in scope
        ["A"]          | []             | []                  || SKIPPED     // not in scope
        ["A"]          | ["A"]          | [A]                 || SKIPPED     // excluded and included
        ["A"]          | ["A"]          | []                  || SKIPPED     // empty scope, scoped execution
        ["A", "B"]     | ["C", "D"]     | [A]                 || NOT_SKIPPED // in scope, multiple scopes
        ["A", "B"]     | ["C", "D"]     | [C]                 || SKIPPED     // not in scope, multiple scopes
        ["A"]          | []             | [A, C]              || NOT_SKIPPED // included, multiple test scopes
        []             | ["A"]          | [A, C]              || SKIPPED     // excluded, multiple test scopes
    }

    def "using something else, but Strings or SpecScope subclasses in config should cause an exception"(
        List<Object> includedScopes, List<Object> excludedScopes) {
        given:
        setConfigParameters(includedScopes, excludedScopes)

        when:
        scopeExtension.visitSpecAnnotation(Mock(Scope), specInfoMock)
        scopeExtension.visitFeatureAnnotation(Mock(Scope), featureInfoMock)

        then:
        thrown(IllegalArgumentException)

        where:
        includedScopes | excludedScopes
        [new Object()] | []
        []             | [new Object()]
    }

    def cleanup() {
        resetSystemParameters()
    }

    private void setSystemParameters(Set<Class<? extends SpecScope>> includedScopes, Set<Class<? extends SpecScope>> excludedScopes) {
        System.setProperty("spock.scopes", includedScopes*.simpleName.join(","))
        System.setProperty("spock.excludedScopes", excludedScopes*.simpleName.join(","))
    }

    private void resetSystemParameters() {
        System.setProperty("spock.scopes", originalIncludedScopes)
        System.setProperty("spock.excludedScopes", originalExcludedScopes)
    }

    private void setConfigParameters(def includedScopes, def excludedScopes) {
        configMock.get("includedScopes") >> includedScopes
        configMock.get("excludedScopes") >> excludedScopes
    }
}
