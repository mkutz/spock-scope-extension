package de.assertagile.spockframework.extensions

import de.assertagile.spockframework.extensions.PseudoScopes.AllBut
import de.assertagile.spockframework.extensions.PseudoScopes.Unscoped
import de.assertagile.spockframework.extensions.SpecScopes.A
import de.assertagile.spockframework.extensions.SpecScopes.B
import de.assertagile.spockframework.extensions.SpecScopes.C
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.PendingFeature
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll
import spock.util.environment.RestoreSystemProperties

import static de.assertagile.spockframework.extensions.ScopeExtensionClassSpec.Included.EXCLUDED
import static de.assertagile.spockframework.extensions.ScopeExtensionClassSpec.Included.INCLUDED

@RestoreSystemProperties
@Scope(A)
class ScopeExtensionClassSpec extends Specification {

    ConfigObject configMock = Mock()

    @Subject
    ScopeExtension scopeExtension = new ScopeExtension(config: configMock)

    FeatureInfo featureInfoMock = Mock() { getName() >> "I am a mocked feature" }
    SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }

    @Unroll("with -Dspock.scopes #includedScopes.simpleName and -Dspock.excludedScopes #excludedScopes.simpleName, a feature or spec with scopes #specOrFeatureScopes.simpleName should be #included")
    void "system parameters should work"(
            List includedScopes, List excludedScopes, List specOrFeatureScopes, Included included) {
        given:
        setSystemParameters(includedScopes, excludedScopes)

        and:
        Scope scopeMock = Mock() { value() >> specOrFeatureScopes }

        when:
        scopeExtension.visitSpecAnnotation(scopeMock, specInfoMock)
        scopeExtension.visitFeatureAnnotation(scopeMock, featureInfoMock)

        then:
        (included ? 0 : 1) * specInfoMock.setExcluded(true)
        (included ? 0 : 1) * featureInfoMock.setExcluded(true)

        where:
        includedScopes | excludedScopes | specOrFeatureScopes || included
        []             | []             | []                  || INCLUDED
        [A]            | []             | []                  || EXCLUDED
        [A, B]         | []             | []                  || EXCLUDED
        []             | [A]            | []                  || EXCLUDED
        []             | [A, B]         | []                  || EXCLUDED

        []             | []             | [A]                 || INCLUDED
        [A]            | []             | [A]                 || INCLUDED
        [A, B]         | []             | [A]                 || INCLUDED
        []             | [A]            | [A]                 || EXCLUDED
        []             | [A, B]         | [A]                 || EXCLUDED

        []             | []             | [C]                 || INCLUDED
        [A]            | []             | [C]                 || EXCLUDED
        [A, B]         | []             | [C]                 || EXCLUDED
        []             | [A]            | [C]                 || INCLUDED
        []             | [A, B]         | [C]                 || INCLUDED
    }

    @Unroll("with -Dspock.scopes #includedScopes.simpleName and -Dspock.excludedScopes #excludedScopes.simpleName, a feature or spec with scopes #specOrFeatureScopes.simpleName should be #included")
    void "unscoped should work"(
            List includedScopes, List excludedScopes, List specOrFeatureScopes, Included included) {
        given:
        setSystemParameters(includedScopes, excludedScopes)

        and:
        Scope scopeMock = Mock() { value() >> specOrFeatureScopes }

        when:
        scopeExtension.visitSpecAnnotation(scopeMock, specInfoMock)
        scopeExtension.visitFeatureAnnotation(scopeMock, featureInfoMock)

        then:
        (included ? 0 : 1) * specInfoMock.setExcluded(true)
        (included ? 0 : 1) * featureInfoMock.setExcluded(true)

        where:
        includedScopes | excludedScopes | specOrFeatureScopes || included
        [Unscoped]     | []             | []                  || INCLUDED
        []             | [Unscoped]     | []                  || EXCLUDED

        [Unscoped]     | []             | [A]                 || EXCLUDED
        []             | [Unscoped]     | [A]                 || INCLUDED

        [Unscoped]     | [A]            | [A]                 || EXCLUDED
        [A]            | [Unscoped]     | [A]                 || INCLUDED
    }

    @PendingFeature
    @Unroll("with -Dspock.scopes #includedScopes.simpleName and -Dspock.excludedScopes #excludedScopes.simpleName, a feature or spec with scopes #specOrFeatureScopes.simpleName should be #included")
    void "all but should work"(
            List includedScopes, List excludedScopes, List specOrFeatureScopes, Included included) {
        given:
        setSystemParameters(includedScopes, excludedScopes)

        and:
        Scope scopeMock = Mock() { value() >> specOrFeatureScopes }

        when:
        scopeExtension.visitSpecAnnotation(scopeMock, specInfoMock)
        scopeExtension.visitFeatureAnnotation(scopeMock, featureInfoMock)

        then:
        (included ? 0 : 1) * specInfoMock.setExcluded(true)
        (included ? 0 : 1) * featureInfoMock.setExcluded(true)

        where:
        includedScopes | excludedScopes | specOrFeatureScopes || included
        [AllBut]       | [A]            | [A]                 || EXCLUDED
        [A]            | [AllBut]       | [A]                 || INCLUDED

        [AllBut]       | [A]            | [B]                 || INCLUDED
        [A]            | [AllBut]       | [B]                 || EXCLUDED

        [AllBut]       | [Unscoped]     | []                  || EXCLUDED
        [Unscoped]     | [AllBut]       | []                  || INCLUDED
        [Unscoped]     | [AllBut]       | []                  || INCLUDED
    }

    private static void setSystemParameters(final Collection includedScopes, final Collection excludedScopes) {
        final List includedScopeStrings = includedScopes.collect { it instanceof Class ? it.simpleName : it }
        final List excludedScopeStrings = excludedScopes.collect { it instanceof Class ? it.simpleName : it }
        System.setProperty("spock.scopes", includedScopeStrings.join(","))
        System.setProperty("spock.excludedScopes", excludedScopeStrings.join(","))
    }

    private void setConfigParameters(includedScopes, excludedScopes) {
        configMock.get("includedScopes") >> includedScopes
        configMock.get("excludedScopes") >> excludedScopes
    }


    enum Included {

        EXCLUDED, INCLUDED

        boolean asBoolean() {
            this == INCLUDED
        }

        String toString() {
            super.toString().toLowerCase()
        }
    }
}
