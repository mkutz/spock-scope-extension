Spock Scope Extension
=====================

[![Build Status](https://travis-ci.org/mkutz/spock-scope-extension.svg?branch=master)](https://travis-ci.org/mkutz/spock-scope-extension) [![Coverage Status](https://img.shields.io/coveralls/mkutz/spock-scope-extension.svg)](https://coveralls.io/r/mkutz/spock-scope-extension) [![Maven Central](https://img.shields.io/maven-central/v/de.assertagile.spockframework.extensions/spock-scope-extension.svg)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22spock-scope-extension%22)

An extension to mark tests as belonging to a (set of) scopes (e.g. a global feature or a major aspect of the application) for the [Spock Framework](http://spockframework.org/).

When executing a Specification marked `@Scope([FeatureA, FeatureB])` and the configured current scope is `[FeatureC, FeatureD]`, the test will be set "ignored" while all tests that are marked with a set of scopes containing at least one `FeatureC` or `FeatureD` will still be executed. Specification not having a `@Scope` annotation will be executed as well.

Usage
-----

For a detailed view on how to use this extension please have a look into the [demonstation folder](https://github.com/mkutz/spock-scope-extension/tree/master/demonstration) of this repository.

Add this as a test dependency to your project.

For instance in your pom.xml:
```xml
<dependency>
  <groupId>de.assertagile.spockframework.extensions</groupId>
  <artifactId>spock-scope-extension</artifactId>
  <version>${spock-scope-extension.version}</version>
  <scope>test</scope>
</dependency>
```

Now you can mark your `Specification`s or their feature methods with the `@Scope(…)` annotation with one or more subclasses of `SpecScope` representing scopes.

When you execute your tests you can pass `-Dspock.scopes=…` to your test execution, all tests that are not annotated with at least one of the given scopes, it will be ignored.

You can as well pass `-Dspock.excludedScopes=…` to ignore all tests that have at least one of the given scopes (since v0.3).

Note that all unscoped `Specification` and feature methods will be ignored when you set scopes (included or exclued). If you want to include unscoped tests, you can use the pseudo scope `UNSCOPED` (since v0.4).

To ease usage in IDE's you can as well create a `SpockScopeConfig.groovy` to your projects resources and add a list of `includedScopes` and `excludedScopes` instead of the `-Dspock.scopes=…` and `-Dspock.excludedScopes=…` parameters:
```groovy
includedScopes = [FeatureA, FeatureC]
excludedScopes = [FeatureB]
```

Note that if neither `includedScopes` nor `excludedScopes` is configured and neither `-Dspock.scopes=…` nor `-Dspock.excludedScopes=…` are set the extension will not ignore any test.

Also note that the `-Dspock.scopes=…` and `-Dspock.excludedScopes=…` parameters will always replace the configuration in `SpockScopeConfig.groovy` if given.

Alternative
-----------

Instead of using this custom extension, you could also use Spock's own (undocumented) runner configuration mechanism.

Check the source in this [demonstration folder](https://github.com/mkutz/spock-scope-extension/tree/master/demonstration-no-extension) for some examples how to use it.

This was derived from [the IncludeExcludeExtensionSpec](https://github.com/spockframework/spock-example/blob/master/src/test/groovy/IncludeExcludeExtensionSpec.groovy) in the [spock-example project](https://github.com/spockframework/spock-example). Checkout the GroovyDoc on the file for more details.