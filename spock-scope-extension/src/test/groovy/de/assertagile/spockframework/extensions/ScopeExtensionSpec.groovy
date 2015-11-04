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

import static de.assertagile.spockframework.extensions.ScopeExtensionSpec.Skipped.NOT_SKIPPED
import static de.assertagile.spockframework.extensions.ScopeExtensionSpec.Skipped.SKIPPED

@Scope(A)
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
    def "only tests in included scopes should be executed, while tests in excluded scopes should always be skipped in executions scoped via system parameters"(
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
        []             | []             | []                  || NOT_SKIPPED // unscoped execution, no scope set
    }

    @Unroll("with includedScopes = #includedScopes and excludedScopes = #excludedScopes, a feature or spec with scopes #specOrFeatureScopes should be #skipped")
    def "only tests in included scopes should be executed, while tests in excluded scopes should always be skipped in executions scoped via config parameters"(
        Set<String> includedScopes, Set<String> excludedScopes, Set<String> specOrFeatureScopes, Skipped skipped) {
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
        []             | []             | []                  || NOT_SKIPPED // unscoped execution, no scope set
    }

    @Unroll("with -Dspock.scopes #includedScopes and -Dspock.excludedScopes #excludedScopes, an unscoped Specification Or feature should be #skipped")
    def "unscoped features should be ignored in executions scoped via system parameters"(
        Set<Class<? extends SpecScope>> includedScopes, Set<Class<? extends SpecScope>> excludedScopes, Skipped skipped) {
        given:
        setSystemParameters(includedScopes, excludedScopes)

        and:
        specInfoMock.getAnnotation(Scope) >> null
        FeatureInfo unscopedFeatureInfoMock = Mock() { getFeatureMethod() >> Mock(MethodInfo) { getAnnotation(Scope) >> null }}
        specInfoMock.getAllFeatures() >> [unscopedFeatureInfoMock]

        when:
        scopeExtension.visitSpec(specInfoMock)

        then:
        (skipped ? 1 : 0) * specInfoMock.setSkipped(true)

        where:
        includedScopes | excludedScopes || skipped
        []             | []             || NOT_SKIPPED // unscoped execution
        [A]            | []             || SKIPPED     // scoped execution with included scopes
        []             | [A]            || SKIPPED     // scoped execution with excluded scopes
        [A]            | [B]            || SKIPPED     // scoped execution with included and excluded scopes
    }

    @Unroll("with includedScopes = #includedScopes and excludedScopes = #excludedScopes, an unscoped Specification Or feature should be #skipped")
    def "unscoped Specifications should be ignored in executions scoped via config"(
        List<Class<? extends SpecScope>> includedScopes, List<Class<? extends SpecScope>> excludedScopes, Skipped skipped) {
        given:
        setConfigParameters(includedScopes, excludedScopes)

        and:
        specInfoMock.getAnnotation(Scope) >> null
        FeatureInfo unscopedFeatureInfoMock = Mock() { getFeatureMethod() >> Mock(MethodInfo) { getAnnotation(Scope) >> null }}
        specInfoMock.getAllFeatures() >> [unscopedFeatureInfoMock]

        when:
        scopeExtension.visitSpec(specInfoMock)

        then:
        (skipped ? 1 : 0) * specInfoMock.setSkipped(true)

        where:
        includedScopes | excludedScopes || skipped
        []             | []             || NOT_SKIPPED // unscoped execution
        ["A"]          | []             || SKIPPED     // scoped execution with included scopes
        []             | ["A"]          || SKIPPED     // scoped execution with excluded scopes
        ["A"]          | ["B"]          || SKIPPED     // scoped execution with included and excluded scopes
    }

    def "an unscoped Specification with scoped features should not be ignored"() {
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
        0 * specInfoMock.setSkipped(true)
        1 * unscopedFeatureInfoMock.setSkipped(true)
        0 * scopedFeatureInfoMock.setSkipped(true)
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

    def "scoping a fixture method will throw an exception"() {
        when:
        scopeExtension.visitFixtureAnnotation(Mock(Scope) { annotationType() >> Scope }, Mock(MethodInfo))

        then:
        InvalidSpecException e = thrown()
        e.message == "@Scope may not be applied to fixture methods"
    }

    def "scoping a field throw will an exception"() {
        when:
        scopeExtension.visitFieldAnnotation(Mock(Scope) { annotationType() >> Scope }, Mock(FieldInfo))

        then:
        InvalidSpecException e = thrown()
        e.message == "@Scope may not be applied to fields"
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
