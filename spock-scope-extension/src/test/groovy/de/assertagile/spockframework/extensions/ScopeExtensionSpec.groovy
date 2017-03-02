package de.assertagile.spockframework.extensions

import de.assertagile.spockframework.extensions.SpecScopes.A
import de.assertagile.spockframework.extensions.SpecScopes.B
import de.assertagile.spockframework.extensions.SpecScopes.C
import de.assertagile.spockframework.extensions.SpecScopes.D
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.SpecInfo
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static de.assertagile.spockframework.extensions.ScopeExtensionSpec.Excluded.EXCLUDED
import static de.assertagile.spockframework.extensions.ScopeExtensionSpec.Excluded.NOT_EXCLUDED

@Scope(A)
class ScopeExtensionSpec extends Specification {

    String originalIncludedScopes = System.getProperty("spock.scopes", "")
    String originalExcludedScopes = System.getProperty("spock.excludedScopes", "")

    ConfigObject configMock = Mock()

    enum Excluded {
        EXCLUDED, NOT_EXCLUDED

        boolean asBoolean() { this == EXCLUDED }

        String toString() { super.toString().toLowerCase().replace("_", " ") }
    }

    @Subject
    ScopeExtension scopeExtension = new ScopeExtension(config: configMock)

    FeatureInfo featureInfoMock = Mock() { getName() >> "I am a mocked feature" }
    SpecInfo specInfoMock = Mock() { getName() >> "MockedSpec" }

    @Unroll("with -Dspock.scopes #includedScopes and -Dspock.excludedScopes #excludedScopes, a feature or spec with scopes #specOrFeatureScopes should be #excluded")
    void "only tests in included scopes should be executed, while tests in excluded scopes should always be excluded in executions scoped via system parameters"(
        Set<Class<? extends SpecScope>> includedScopes, Set<Class<? extends SpecScope>> excludedScopes, Set<Class<? extends SpecScope>> specOrFeatureScopes, Excluded excluded) {
        given:
        setSystemParameters(includedScopes, excludedScopes)

        and:
        Scope scopeMock = Mock() { value() >> specOrFeatureScopes }

        when:
        scopeExtension.visitSpecAnnotation(scopeMock, specInfoMock)
        scopeExtension.visitFeatureAnnotation(scopeMock, featureInfoMock)

        then:
        (excluded ? 1 : 0) * specInfoMock.setExcluded(true)
        (excluded ? 1 : 0) * featureInfoMock.setExcluded(true)

        where:
        includedScopes | excludedScopes | specOrFeatureScopes || excluded
        []             | []             | []                  || NOT_EXCLUDED // execution not scoped
        [A]            | []             | [A]                 || NOT_EXCLUDED // in scope
        [A]            | []             | []                  || EXCLUDED     // not in scope
        [A]            | [A]            | [A]                 || EXCLUDED     // excluded and included
        [A]            | [A]            | []                  || EXCLUDED     // empty scope, scoped execution
        [A, B]         | [C, D]         | [A]                 || NOT_EXCLUDED // in scope, multiple scopes
        [A, B]         | [C, D]         | [C]                 || EXCLUDED     // not in scope, multiple scopes
        [A]            | []             | [A, C]              || NOT_EXCLUDED // included, multiple test scopes
        []             | [A]            | [A, C]              || EXCLUDED     // excluded, multiple test scopes
        []             | []             | []                  || NOT_EXCLUDED // unscoped execution, no scope set
    }

    @Unroll("with includedScopes = #includedScopes and excludedScopes = #excludedScopes, a feature or spec with scopes #specOrFeatureScopes should be #excluded")
    void "only tests in included scopes should be executed, while tests in excluded scopes should always be excluded in executions scoped via config parameters"(
        Set<String> includedScopes, Set<String> excludedScopes, Set<String> specOrFeatureScopes, Excluded excluded) {
        given:
        setConfigParameters(includedScopes, excludedScopes)

        and:
        Scope scopeMock = Mock() { value() >> specOrFeatureScopes }

        when:
        scopeExtension.visitSpecAnnotation(scopeMock, specInfoMock)
        scopeExtension.visitFeatureAnnotation(scopeMock, featureInfoMock)

        then:
        (excluded ? 1 : 0) * specInfoMock.setExcluded(true)
        (excluded ? 1 : 0) * featureInfoMock.setExcluded(true)

        where:
        includedScopes | excludedScopes | specOrFeatureScopes || excluded
        ["A"]          | []             | [A]                 || NOT_EXCLUDED // in scope
        ["A"]          | []             | []                  || EXCLUDED     // not in scope
        ["A"]          | ["A"]          | [A]                 || EXCLUDED     // excluded and included
        ["A"]          | ["A"]          | []                  || EXCLUDED     // empty scope, scoped execution
        ["A", "B"]     | ["C", "D"]     | [A]                 || NOT_EXCLUDED // in scope, multiple scopes
        ["A", "B"]     | ["C", "D"]     | [C]                 || EXCLUDED     // not in scope, multiple scopes
        ["A"]          | []             | [A, C]              || NOT_EXCLUDED // included, multiple test scopes
        []             | ["A"]          | [A, C]              || EXCLUDED     // excluded, multiple test scopes
        []             | []             | []                  || NOT_EXCLUDED // unscoped execution, no scope set
        ["UNSCOPED"]   | []             | []                  || NOT_EXCLUDED // unscoped included, unscoped spec
        []             | ["UNSCOPED"]   | []                  || EXCLUDED     // unscoped excluded, unscoped spec
    }

    @Unroll("with -Dspock.scopes #includedScopes and -Dspock.excludedScopes #excludedScopes, an unscoped Specification Or feature should be #excluded")
    void "unscoped features should be ignored in executions scoped via system parameters"(
        Set<Class<? extends SpecScope>> includedScopes, Set<Class<? extends SpecScope>> excludedScopes, Excluded excluded) {
        given:
        setSystemParameters(includedScopes, excludedScopes)

        and:
        specInfoMock.getAnnotation(Scope) >> null
        FeatureInfo unscopedFeatureInfoMock = Mock() { getFeatureMethod() >> Mock(MethodInfo) { getAnnotation(Scope) >> null }}
        specInfoMock.getAllFeatures() >> [unscopedFeatureInfoMock]

        when:
        scopeExtension.visitSpec(specInfoMock)

        then:
        (excluded ? 1 : 0) * specInfoMock.setExcluded(true)

        where:
        includedScopes | excludedScopes || excluded
        []             | []             || NOT_EXCLUDED // unscoped execution
        [A]            | []             || EXCLUDED     // scoped execution with included scopes
        []             | [A]            || EXCLUDED     // scoped execution with excluded scopes
        [A]            | [B]            || EXCLUDED     // scoped execution with included and excluded scopes
    }

    @Unroll("with includedScopes = #includedScopes and excludedScopes = #excludedScopes, an unscoped Specification Or feature should be #excluded")
    void "unscoped Specifications should be ignored in executions scoped via config"(
        List<Class<? extends SpecScope>> includedScopes, List<Class<? extends SpecScope>> excludedScopes, Excluded excluded) {
        given:
        setConfigParameters(includedScopes, excludedScopes)

        and:
        specInfoMock.getAnnotation(Scope) >> null
        FeatureInfo unscopedFeatureInfoMock = Mock() { getFeatureMethod() >> Mock(MethodInfo) { getAnnotation(Scope) >> null }}
        specInfoMock.getAllFeatures() >> [unscopedFeatureInfoMock]

        when:
        scopeExtension.visitSpec(specInfoMock)

        then:
        (excluded ? 1 : 0) * specInfoMock.setExcluded(true)

        where:
        includedScopes | excludedScopes || excluded
        []             | []             || NOT_EXCLUDED // unscoped execution
        ["A"]          | []             || EXCLUDED     // scoped execution with included scopes
        []             | ["A"]          || EXCLUDED     // scoped execution with excluded scopes
        ["A"]          | ["B"]          || EXCLUDED     // scoped execution with included and excluded scopes
        ["UNSCOPED"]   | []             || NOT_EXCLUDED // unscoped included
        []             | ["UNSCOPED"]   || EXCLUDED     // unscoped excluded
    }

    void "an unscoped Specification with scoped features should not be ignored"() {
        given:
        setConfigParameters([A], [B])

        and:
        specInfoMock.getAnnotation(Scope) >> null
        FeatureInfo unscopedFeatureInfoMock = Mock() { getFeatureMethod() >> Mock(MethodInfo) { getAnnotation(Scope) >> null }}
        FeatureInfo scopedFeatureInfoMock = Mock() { getFeatureMethod() >> Mock(MethodInfo) { getAnnotation(Scope) >> Mock(Scope) }}
        specInfoMock.getAllFeatures() >> [unscopedFeatureInfoMock, scopedFeatureInfoMock]

        when:
        scopeExtension.visitSpec(specInfoMock)

        then:
        0 * specInfoMock.setExcluded(true)
        1 * unscopedFeatureInfoMock.setExcluded(true)
        0 * scopedFeatureInfoMock.setExcluded(true)
    }

    void "using something else, but Strings or SpecScope subclasses in config should cause an exception"(
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

    void "scoping a fixture method will throw an exception"() {
        when:
        scopeExtension.visitFixtureAnnotation(Mock(Scope) { annotationType() >> Scope }, Mock(MethodInfo))

        then:
        InvalidSpecException e = thrown()
        e.message == "@Scope may not be applied to fixture methods"
    }

    void "scoping a field throw will an exception"() {
        when:
        scopeExtension.visitFieldAnnotation(Mock(Scope) { annotationType() >> Scope }, Mock(FieldInfo))

        then:
        InvalidSpecException e = thrown()
        e.message == "@Scope may not be applied to fields"
    }

    void cleanup() {
        resetSystemParameters()
    }

    private static void setSystemParameters(Set<Class<? extends SpecScope>> includedScopes,
            Set<Class<? extends SpecScope>> excludedScopes) {
        System.setProperty("spock.scopes", includedScopes*.simpleName.join(","))
        System.setProperty("spock.excludedScopes", excludedScopes*.simpleName.join(","))
    }

    private void resetSystemParameters() {
        System.setProperty("spock.scopes", originalIncludedScopes)
        System.setProperty("spock.excludedScopes", originalExcludedScopes)
    }

    private void setConfigParameters(includedScopes, excludedScopes) {
        configMock.get("includedScopes") >> includedScopes
        configMock.get("excludedScopes") >> excludedScopes
    }
}
