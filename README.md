Spock Scope Extension
=====================

[![Build Status](https://travis-ci.org/mkutz/spock-scope-extension.svg?branch=master)](https://travis-ci.org/mkutz/spock-scope-extension) [![Coverage Status](https://img.shields.io/coveralls/mkutz/spock-scope-extension.svg)](https://coveralls.io/r/mkutz/spock-scope-extension)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.assertagile.spockframework.extensions/spock-scope-extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.assertagile.spockframework.extensions/spock-scope-extension)

An extension to mark tests as belonging to a (set of) scopes (e.g. a global feature or a major aspect of the application) for the [Spock Framework](http://spockframework.org/).

When executing a Specification marked `@Scope(["feature a", "feature b"])` and the configured current scope is `["feature c", "feature d"]`, the test will be set "ignored" while all tests that are marked with a set of scopes containing at least one `"feature c"` or `"feature d"` will still be executed. Specification not having a `@Scope` annotation will be executed as well.

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

Now you can mark your `Specification`s with the `@Scope(…)` annotation with one or more strings representing scopes.

When you execute your tests you can pass `-Dspock.scopes=…` to your test execution, all tests that are not annotated with at least one of the given scopes, it will be ignored.

To ease usage in IDE's you can as well create a `SpockScopeConfig.groovy` to your projects resources and add a list of `currentScopes` instead of the `-Dspock.scopes=…` parameter:
```groovy
currentScopes = ["feature a", "feature c"]
```

Note that if `currentScopes` is empty or not configured at all the extension will not ignore any test.

Also note that the `-Dspock.scopes=…` parameter will always replace the configuration in `SpockScopeConfig.groovy` if given.

Planned Features for next Version
---------------------------------

* Parameter and configuration for excluded scopes `-Dspock.excludedScopes`
