package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import de.assertagile.spockframework.extensions.SpecScopes.A
import de.assertagile.spockframework.extensions.SpecScopes.B
import de.assertagile.spockframework.extensions.SpecScopes.C
import de.assertagile.spockframework.extensions.SpecScopes.D

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

    @Unroll("with #includedScopes included and #excludedScopes excluded, a feature or spec with scopes #specOrFeatureScopes should be #skipped")
    def "only tests in included scopes should be executed, while tests in excluded scopes should always be skipped"(
            List<String> includedScopes, List<String> excludedScopes, List<String> specOrFeatureScopes, Skipped skipped) {
        given:
        setIncludedScopes(includedScopes)
        setExcludedScopes(excludedScopes)

        and:
        FeatureInfo featureInfoMock = Mock() { getName() >> "I am a mocked feature" }
        SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }

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
        []             | []             | []                  || Skipped.NOT_SKIPPED // execution not scoped
        [A]            | []             | [A]                 || Skipped.NOT_SKIPPED // in scope
        [A]            | []             | []                  || Skipped.SKIPPED     // not in scope
        [A]            | [A]            | [A]                 || Skipped.SKIPPED     // excluded and included
        [A]            | [A]            | []                  || Skipped.SKIPPED     // empty scope, scoped execution
        [A, B]         | [C, D]         | [A]                 || Skipped.NOT_SKIPPED // in scope, multiple scopes
        [A, B]         | [C, D]         | [C]                 || Skipped.SKIPPED     // not in scope, multiple scopes
        [A]            | []             | [A, C]              || Skipped.NOT_SKIPPED // included, multiple test scopes
        []             | [A]            | [A, C]              || Skipped.SKIPPED     // excluded, multiple test scopes
    }

    def cleanup() {
        resetScopes()
    }

    private void setIncludedScopes(List<? extends Class<SpecScope>> newScopes) {
        System.setProperty("spock.scopes", newScopes*.simpleName.join(","))
    }

    private void setExcludedScopes(List<? extends SpecScope> newExcludedScopes) {
        System.setProperty("spock.excludedScopes", newExcludedScopes*.simpleName.join(","))
    }

    private void resetScopes() {
        System.setProperty("spock.scopes", originalIncludedScopes)
        System.setProperty("spock.excludedScopes", originalExcludedScopes)
    }
}
