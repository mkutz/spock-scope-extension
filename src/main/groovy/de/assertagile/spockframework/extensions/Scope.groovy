package de.assertagile.spockframework.extensions

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * <p>
 * Indicates the marked {@link spock.lang.Specification} type or feature method as belonging to a set of scopes.
 * </p>
 *
 * <p>
 * The extension will automatically ignore features and specifications not within the currently executed scope.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(ScopeExtension)
public @interface Scope {

    /**
     * @return a scope or a set of scopes.
     */
    String[] value();
}