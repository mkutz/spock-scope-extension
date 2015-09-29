Spock Scope Extension
=====================

[![Build Status](https://travis-ci.org/mkutz/spock-scope-extension.svg?branch=master)](https://travis-ci.org/mkutz/spock-scope-extension) [![Coverage Status](https://img.shields.io/coveralls/mkutz/spock-scope-extension.svg)](https://coveralls.io/r/mkutz/spock-scope-extension)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.assertagile.spockframework.extensions/spock-scope-extension/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.assertagile.spockframework.extensions/spock-scope-extension)

An extension to mark tests as belonging to a (set of) scopes (e.g. a global feature or a major aspect of the application) for the Spock Framework (see http://spockframework.org/).

When executing a Specification marked `@Scope(["a", "b"])` and the configured current scope is `["c", "d"]`, the test will be set "ignored" while all tests that are marked with a set of scopes containing at least one `"c"` or `"d"`.