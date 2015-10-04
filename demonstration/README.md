Demonstation
============

This folder contains an example project demonstrating how to use the [Spock Scope Extension](https://github.com/mkutz/spock-scope-extension).

Scoped Specification
--------------------

As you can see the [ScopedSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedSpec.groovy) is annotated with `@Scope` and a list of scopes.

The scope strings are defined in the [TestScopes](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/TestScopes.groovy) class to prevent duplicates and simplify refactorings.

Try to execute `mvn test -Dspock.scopes="feature c"`. As there is no specification for that scope, all tests will be ignored.

Try `-Dspock.scopes="feature a, feature b"` and you will see that the `Specification` is executed.

Scoped Features
---------------

The `Specification`'s feature methods are annotated as well: one is scoped to "feature a", one to "feature b" and one is not scoped at all.

Try `-Dspock.scopes="feature a"` and you will see that the `Specification` is not ignored but only the first and the last feature method are being executed, while the second is ignored since it is scoped to "feature b".

Config File
-----------

If you use an IDE it is quite inconviniet to use `-D` parameters. For this case you can use [SpockScopeConfig.groovy](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/resources/SpockScopeConfig.groovy) to set your scope.

As you can see the `currentScopes` variable is set to "feature a" in the example file. Therefore if you execute the whole `Specification` in your IDE, the second feature method is ignored (by the way: it also is ignored if you `mvn test` without the `-Dspock.scopes` parameter).

Try to change the file's content and you will see how the behaviour changes.
