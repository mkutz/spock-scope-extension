package de.assertagile.spockframework.extensions

import groovy.util.logging.Slf4j
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
@Slf4j
public class ScopeExtension extends AbstractAnnotationDrivenExtension<Scope> {

    /** {@link ConfigObject} for the extension. */
    private ConfigObject config

    /** The {@link List} of currently selected scopes. */
    private List<String> currentScopes

    /**
     * Standard constructor.
     */
    public ScopeExtension() {}

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
        if (!isInCurrentScope(annotation)) {
            log.debug("Skipping ${spec.name} for it is not in current scope (${currentScopes})")
            spec.setSkipped(true)
        }
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
        if (!isInCurrentScope(annotation)) {
            log.debug("Skipping ${feature.name} for it is not in current scope (${currentScopes})")
            feature.setSkipped(true)
        }
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
            if (System.getProperty("spock.scopes")) {
                currentScopes = System.getProperty("spock.scopes")?.split(/\s*,\s*/)
            } else {
                currentScopes = getConfig().get("currentScopes") ?: []
            }
        }
        return currentScopes
    }

    private isInCurrentScope(Scope annotation) {
        if (!getCurrentScopes()) return true
        List<String> specOrFeatureScopes = annotation.value() ?: []
        return !getCurrentScopes().disjoint(specOrFeatureScopes)
    }
}