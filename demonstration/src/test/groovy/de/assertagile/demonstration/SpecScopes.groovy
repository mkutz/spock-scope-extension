package de.assertagile.demonstration

import de.assertagile.spockframework.extensions.SpecScope

/**
 * <p>
 * A collection of scopes to be used in <code>@Scope</code> annotations in this project.
 * </p>
 *
 * <p>
 * This is not necessary but a good practice to prevent duplicate scope definitions in larger projects.
 * </p>
 */
class SpecScopes {

    class FeatureA implements SpecScope {}
    class FeatureB implements SpecScope {}
    class FeatureC implements SpecScope {}
    class FeatureD implements SpecScope {}
}
