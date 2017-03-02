package de.assertagile.spockframework.extensions

import groovy.util.logging.Slf4j
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.IExcludable
import org.spockframework.runtime.model.MethodInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * <p>
 * Extension for the <a href="http://spockframework.org">Spock Framework</a> to be able to mark specifications or
 * single features to belong to a (set of) scopes.
 * </p>
 *
 * <p>
 * Just mark a single feature method or a whole {@link spock.lang.Specification} with the annotation {@link Scope} and
 * a list of configured scopes.
 * </p>
 */
@Slf4j
class ScopeExtension implements IAnnotationDrivenExtension<Scope>, IGlobalExtension {

    /** {@link ConfigObject} for the extension. */
    private ConfigObject config

    /** The {@link Set} of currently included scopes. */
    private Set<String> includedScopes

    /** The {@link Set} of currently excluded scopes. */
    private Set<String> excludedScopes

    /**
     * Standard constructor.
     */
    ScopeExtension() {}

    /**
     * Called when a marked specification type is visited. Sets the whole {@link SpecInfo} to be ignored if not in one
     * of the included scopes.
     *
     * @param annotation
     *          the {@link Scope} annotation at the specification type. Contains the specification's scopes.
     * @param spec
     *          the {@link SpecInfo} for the visited specification class.
     */
    @Override
    void visitSpecAnnotation(Scope annotation, SpecInfo spec) {
        visitExcludable(annotation, spec, spec.name)
    }

    /**
     * Called when a marked feature method is visited. Sets the {@link FeatureInfo} to be ignored if not in one of the
     * included scopes.
     *
     * @param annotation
     *          the {@link Scope} annotation at the specification type. Contains the specification's scopes.
     * @param feature
     *          the {@link FeatureInfo} for the visited feature method.
     */
    @Override
    void visitFeatureAnnotation(Scope annotation, FeatureInfo feature) {
        visitExcludable(annotation, feature, feature.name)
    }

    /**
     * Called when a marked fixture method is visited. Will just throw an {@link InvalidSpecException}.
     *
     * @param annotation
     *          the {@link Scope} annotation at the fixture method.
     * @param fixtureMethod
     *          the {@link MethodInfo} for the visited fixture method.
     */
    @Override
    void visitFixtureAnnotation(Scope annotation, MethodInfo fixtureMethod) {
        throw new InvalidSpecException("@%s may not be applied to fixture methods")
                .withArgs(annotation.annotationType().getSimpleName());
    }

    /**
     * Called when a marked field is visited. Will just throw an {@link InvalidSpecException}.
     *
     * @param annotation
     *          the {@link Scope} annotation at the field.
     * @param fixtureMethod
     *          the {@link FieldInfo} for the visited field.
     */
    @Override
    void visitFieldAnnotation(Scope annotation, FieldInfo field) {
        throw new InvalidSpecException("@%s may not be applied to fields")
                .withArgs(annotation.annotationType().getSimpleName());
    }

    /**
     * Called when this extension is started as {@link IGlobalExtension}.
     */
    @Override
    void start() {
        if (isScopedExecution()) {
            log.debug("This is a scoped execution")
        } else {
            log.info("This is an unscoped execution, scopes will be ignored")
        }
    }

    /**
     * Called when a {@link SpecInfo} is visited by this extension. Generally only handling unscoped
     * {@link spock.lang.Specification}s and feature methods. Everything else is handled by
     * {@link #visitSpecAnnotation} and {@link #visitFeatureAnnotation}.
     *
     * @param spec
     *          the visited {@link SpecInfo}.
     */
    @Override
    void visitSpec(SpecInfo spec) {
        log.debug("Visiting ${spec.name}")
        if (!isUnscopedIncluded() && isScopedExecution() && !spec.getAnnotation(Scope)) {
            List<FeatureInfo> unscopedFeatures = spec.allFeatures.findAll { !it.featureMethod.getAnnotation(Scope) }
            if (unscopedFeatures == spec.allFeatures) {
                log.info("Excluding \"${spec.name}\" for it is not scoped and this is a scoped run")
                spec.setExcluded(true)
            } else {
                unscopedFeatures.each { FeatureInfo feature ->
                    log.info("Excluding \"${feature.name}\" for it is not scoped and this is a scoped run")
                    feature.setExcluded(true)
                }
            }
        }
    }

    /**
     * Called when this extension is stopped as {@link IGlobalExtension}.
     */
    @Override
    void stop() {
    }

    private void visitExcludable(Scope annotation, IExcludable excludable, String name) {
        if (!isInIncludedScopes(annotation) || isInExcludedScopes(annotation)) {
            if (!isInIncludedScopes(annotation)) {
                log.info("Excluding \"${name}\" for its scope ${annotation?.value()*.simpleName ?: []} is not in " +
                        "included scope (${includedScopes})")
            } else if (isInExcludedScopes(annotation)) {
                log.info("Excluding \"${name}\" for its scope ${annotation?.value()*.simpleName ?: []} is in " +
                        "excluded scope (${excludedScopes})")
            }
            excludable.setExcluded(true)
        }
        log.debug("Including \"${name}\" for its scope ${annotation?.value()*.simpleName ?: []} is in included scope " +
                "(${includedScopes}) and not in excluded scope (${excludedScopes})")
    }

    private ConfigObject getConfig() {
        if (!config) {
            URL configUrl = getClass().getClassLoader().getResource("SpockScopeConfig.groovy")
            config = configUrl ? new ConfigSlurper().parse(configUrl) : new ConfigObject()
        }
        return config
    }

    private Set<String> getIncludedScopes() {
        if (includedScopes == null) {
            includedScopes = getScopes(Parameter.INCLUDED)*.toLowerCase()
        }
        return includedScopes
    }

    private Set<String> getExcludedScopes() {
        if (excludedScopes == null) {
            excludedScopes = getScopes(Parameter.EXCLUDED)*.toLowerCase()
        }
        return excludedScopes
    }

    private Set<String> getScopes(Parameter which) {
        Set<String> scopes
        if (System.getProperty(which.systemProperty)) {
            scopes = System.getProperty(which.systemProperty)?.split(/\s*,\s*/)
        } else {
            scopes = (getConfig().get((which.configParameter) ?: [])).collect {
                if (it instanceof Class<? extends SpecScope>) {
                    return it.simpleName
                } else if (it instanceof String) {
                    return it
                } else {
                    throw new IllegalArgumentException("Configured scopes must be either strings or subclasses of " +
                            "SpecScope! \"${it}\" is ${it.class.simpleName}.")
                }
            }
        }
        return scopes
    }

    private boolean isInIncludedScopes(Scope annotation) {
        if (!getIncludedScopes()) {
            return true
        }
        List<Class<? extends SpecScope>> specOrFeatureScopes = (annotation?.value() ?: []) as List
        if (!specOrFeatureScopes && isUnscopedIncluded()) {
            return true
        }
        return !getIncludedScopes().disjoint(specOrFeatureScopes*.simpleName*.toLowerCase())
    }

    private boolean isInExcludedScopes(Scope annotation) {
        if (!getExcludedScopes()) {
            return false
        }
        List<Class<? extends SpecScope>> specOrFeatureScopes = (annotation?.value() ?: []) as List
        if (!specOrFeatureScopes && !isUnscopedIncluded()) {
            return true
        }
        return !getExcludedScopes().disjoint(specOrFeatureScopes*.simpleName*.toLowerCase())
    }

    private boolean isUnscopedIncluded() {
        getIncludedScopes().contains(PseudoScopes.Unscoped.simpleName.toLowerCase()) &&
                !getExcludedScopes().contains(PseudoScopes.Unscoped.simpleName.toLowerCase())
    }

    private boolean isScopedExecution() {
        this.getExcludedScopes() || this.getIncludedScopes()
    }

    private enum Parameter {

        INCLUDED("spock.scopes", "includedScopes"),
        EXCLUDED("spock.excludedScopes", "excludedScopes")

        final String systemProperty
        final String configParameter

        private Parameter(String systemProperty, String configParameter) {
            this.configParameter = configParameter
            this.systemProperty = systemProperty
        }
    }
}
