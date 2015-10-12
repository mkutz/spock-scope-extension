package de.assertagile.spockframework.extensions

import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

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
        setIncludedScopes(includedScopes.join(","))
        setExcludedScopes(excludedScopes.join(","))
        FeatureInfo featureInfoMock = Mock() { getName() >> "I am a mocked feature" }
        SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }
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
        ["a"]          | []             | ["a"]               || Skipped.NOT_SKIPPED // in scope
        ["a"]          | []             | []                  || Skipped.SKIPPED     // not in scope
        ["a"]          | ["a"]          | ["a"]               || Skipped.SKIPPED     // excluded and included
        ["a"]          | ["a"]          | []                  || Skipped.SKIPPED     // empty scope, scoped execution
        ["a", "b"]     | ["c", "d"]     | ["a"]               || Skipped.NOT_SKIPPED // in scope, multiple scopes
        ["a", "b"]     | ["c", "d"]     | ["c"]               || Skipped.SKIPPED     // not in scope, multiple scopes
        ["a"]          | []             | ["a", "c"]          || Skipped.NOT_SKIPPED // included, multiple test scopes
        []             | ["a"]          | ["a", "c"]          || Skipped.SKIPPED     // excluded, multiple test scopes
    }

    def cleanup() {
        resetScopes()
    }

    private void setIncludedScopes(String newScopes) {
        System.setProperty("spock.scopes", newScopes)
    }

    private void setExcludedScopes(String newExcludedScopes) {
        System.setProperty("spock.excludedScopes", newExcludedScopes)
    }

    private void resetScopes() {
        System.setProperty("spock.scopes", originalIncludedScopes)
        System.setProperty("spock.excludedScopes", originalExcludedScopes)
    }
}
