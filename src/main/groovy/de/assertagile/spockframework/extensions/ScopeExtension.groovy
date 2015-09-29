package de.assertagile.spockframework.extensions

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
import org.spockframework.runtime.model.FeatureInfo
import org.spockframework.runtime.model.SpecInfo

/**
 * <p>
 * Extension for the <a href="http://spockframework.org">Spock Framework</a> to be able to mark specifications or
 * single features to belong to a (set of) scopes.
 * </p>
 *
 * <p>
 * Just mark a singe feature method or a whole {@link spock.lang.Specification} with the annotation {@link Scope} and
 * a list of configured scopes.
 * </p>
 */
public class ScopeExtension extends AbstractAnnotationDrivenExtension<Scope> {

    /** {@link ConfigObject} for the extension. */
    private ConfigObject config
    private List<String> currentScopes
    private List<String> scopes

    /**
     * Standard constructor.
     */
    public ScopeExtension() {
    }

    private ConfigObject getConfig() {
        if (!config) {
            URL configUrl = getClass().getClassLoader().getResource("SpockScopeConfig.groovy")
            config = configUrl ? new ConfigSlurper().parse(configUrl) : new ConfigObject()
        }
        return config
    }

    private List<String> getCurrentScopes() {
        if (currentScopes == null) {
            currentScopes = getConfig().get("currentScopes") ?: []
        }
        return currentScopes
    }

    private List<String> getScopes() {
        if (scopes == null) {
            scopes = getConfig().get("scopes")?: []
        }
        return scopes
    }

    private isInCurrentScope(Scope annotation) {
        if (!getCurrentScopes()) return true
        List<String> specOrFeatureScopes = annotation.value() ?: []
        if (!getScopes().containsAll(specOrFeatureScopes)) {
            throw new Exception("Specified scopes ${specOrFeatureScopes - getScopes()} are not configured!")
        }
        return !getCurrentScopes().disjoint(specOrFeatureScopes)
    }

    /**
     * Called when a marked specification type is visited. Sets the whole {@link SpecInfo} to be ignored if not in one
     * of the current scopes.
     *
     * @param annotation
     *          the {@link Scope} annotation at the specification type. Contains the specification's scopes.
     * @param spec
     *          the {@link SpecInfo} for the visited specification class.
     */
    public void visitSpecAnnotation(Scope annotation, SpecInfo spec) {
        spec.setSkipped(!isInCurrentScope(annotation))
    }

    /**
     * Called when a marked feature method is visited. Sets the {@link FeatureInfo} to be ignored if not in one of the
     * current scopes.
     *
     * @param annotation
     *          the {@link Scope} annotation at the specification type. Contains the specification's scopes.
     * @param feature
     *          the {@link FeatureInfo} for the visited feature method.
     */
    public void visitFeatureAnnotation(Scope annotation, FeatureInfo feature) {
        feature.setSkipped(!isInCurrentScope(annotation))
    }
}