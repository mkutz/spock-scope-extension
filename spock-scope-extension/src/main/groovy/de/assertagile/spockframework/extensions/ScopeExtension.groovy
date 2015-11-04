package de.assertagile.spockframework.extensions

import groovy.util.logging.Slf4j
import org.spockframework.runtime.InvalidSpecException
import org.spockframework.runtime.extension.IAnnotationDrivenExtension
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.FieldInfo
import org.spockframework.runtime.model.ISkippable
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
public class ScopeExtension implements IAnnotationDrivenExtension<Scope>, IGlobalExtension {

    /** {@link ConfigObject} for the extension. */
    private ConfigObject config

    /** The {@link Set} of currently included scopes. */
    private Set<String> includedScopes

    /** The {@link Set} of currently excluded scopes. */
    private Set<String> excludedScopes

    /**
     * Standard constructor.
     */
    public ScopeExtension() {}

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
    public void visitSpecAnnotation(Scope annotation, SpecInfo spec) {
        visitSkippable(annotation, spec, spec.name)
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
    public void visitFeatureAnnotation(Scope annotation, FeatureInfo feature) {
        visitSkippable(annotation, feature, feature.name)
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
    public void visitFixtureAnnotation(Scope annotation, MethodInfo fixtureMethod) {
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
    public void visitFieldAnnotation(Scope annotation, FieldInfo field) {
        throw new InvalidSpecException("@%s may not be applied to fields")
            .withArgs(annotation.annotationType().getSimpleName());
    }

    /**
     * Called when this extension is started as {@link IGlobalExtension}.
     */
    @Override
    void start() {
        if (isScopedExecution()) log.debug("This is a scoped execution")
        else log.info("This is an unscoped execution, scopes will be ignored")
    }

    /**
     * Called when a {@link SpecInfo} is visited by this extension.
     *
     * @param spec
     *          the visited {@link SpecInfo}.
     */
    @Override
    public void visitSpec(SpecInfo spec) {
        log.debug("Visiting ${spec.name}")
        if (!spec.getAnnotation(Scope) && isScopedExecution()) {
            log.info("Skipping \"${spec.name}\" for it is not scoped and this is a scoped run")
            //visitSkippable(null, spec, spec.name)
            spec.setSkipped(true)
        } else {
            log.debug("Not skipping \"${spec.name}\" due to scoped run")
        }
    }

    /**
     * Called when this extension is stopped as {@link IGlobalExtension}.
     */
    @Override
    void stop() {
    }

    private void visitSkippable(Scope annotation, ISkippable skippable, String name) {
        if (!isInIncludedScopes(annotation) || isInExcludedScopes(annotation)) {
            if (!isInIncludedScopes(annotation)) {
                log.info("Skipping \"${name}\" for its scope ${annotation?.value()*.simpleName ?: []} is not in included scope (${includedScopes})")
            } else if (isInExcludedScopes(annotation)) {
                log.info("Skipping \"${name}\" for its scope ${annotation?.value()*.simpleName ?: []} is in excluded scope (${excludedScopes})")
            }
            skippable.setSkipped(true)
        }
        log.debug("Executing \"${name}\" for its scope ${annotation?.value()*.simpleName ?: []} is in included scope (${includedScopes}) and not in excluded scope (${excludedScopes})")
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
            if (System.getProperty("spock.scopes")) {
                includedScopes = System.getProperty("spock.scopes")?.split(/\s*,\s*/)
            } else {
                includedScopes = (getConfig().get("includedScopes") ?: []).collect {
                    if (it instanceof Class<? extends SpecScope>) return it.simpleName
                    else if (it instanceof String) return it
                    else throw new IllegalArgumentException("Configured scopes must be either strings or subclasses of SpecScope! \"${it}\" is ${it.class.simpleName}.")
                }
            }
        }
        return includedScopes
    }

    private Set<String> getExcludedScopes() {
        if (excludedScopes == null) {
            if (System.getProperty("spock.excludedScopes")) {
                excludedScopes = System.getProperty("spock.excludedScopes")?.split(/\s*,\s*/)
            } else {
                excludedScopes = (getConfig().get("excludedScopes") ?: []).collect {
                    if (it instanceof Class<? extends SpecScope>) return it.simpleName
                    else if (it instanceof String) return it
                    else throw new IllegalArgumentException("Configured scopes must be either strings or subclasses of SpecScope! \"${it}\" is ${it.class.simpleName}.")
                }
            }
        }
        return excludedScopes
    }

    private isInIncludedScopes(Scope annotation) {
        if (!getIncludedScopes()) return true
        List<Class<? extends SpecScope>> specOrFeatureScopes = annotation?.value() ?: []
        return !getIncludedScopes().disjoint(specOrFeatureScopes*.simpleName)
    }

    private isInExcludedScopes(Scope annotation) {
        if (!getExcludedScopes()) return false
        List<Class<? extends SpecScope>> specOrFeatureScopes = annotation?.value() ?: []
        return !getExcludedScopes().disjoint(specOrFeatureScopes*.simpleName)
    }

    private isScopedExecution() {
        this.getExcludedScopes() || this.getIncludedScopes()
    }
}
