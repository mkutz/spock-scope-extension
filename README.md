Spock Scope Extension
=====================

[![Build Status](https://travis-ci.org/mkutz/spock-scope-extension.svg?branch=master)](https://travis-ci.org/mkutz/spock-scope-extension) [![Coverage Status](https://img.shields.io/coveralls/mkutz/spock-scope-extension.svg)](https://coveralls.io/r/mkutz/spock-scope-extension)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.assertagile.spockframework.extensions/spock-scope-extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.assertagile.spockframework.extensions/spock-scope-extension)

An extension to mark tests as belonging to a (set of) scopes (e.g. a global feature or a major aspect of the application) for the Spock Framework (see http://spockframework.org/).

When executing a Specification marked `@Scope(["a", "b"])` and the configured current scope is `["c", "d"]`, the test will be set "ignored" while all tests that are marked with a set of scopes containing at least one `"c"` or `"d"`.

Usage
-----

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

Add `SpockScopeConfig.groovy` to your projects resources and add at least a list of possible `scopes` and optinally a list of `currentScopes`:
```groovy
scopes = ["feature a", "feature b", "feature c"]
currentScopes = ["feature a", "feature c"]
```

Note that if `currentScopes` is empty or not configured at all the extension will not ignore any test.

Not yet implemented/Ideas
-------------------------

- `currentScopes`should be a `-D`parameter that can be usens in `mvn test -Dspock.currentScopes=a,b`
- scopes may be better defined as an Enum, so it is refactorable and autocompletable in IDE's.
