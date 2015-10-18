Demonstration
=============

This folder contains an example project demonstrating how to use the [Spock Scope Extension](https://github.com/mkutz/spock-scope-extension).

Scoped Specification
--------------------

As you can see the [ScopedSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedSpec.groovy) is annotated with `@Scope` and a list of scopes.

The scope strings are defined in the [SpecScopes](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/SpecScopes.groovy) class. Note that all scope classes you want to use must implement the SpecScope interface.

Try to execute `mvn test -Dspock.scopes="FeatureC"`. As there is no specification for that scope, all tests will be ignored.

Try `-Dspock.scopes="FeatureA"`, `-Dspock.scopes="FeatureB"` or `-Dspock.scopes="FeatureA, FeatureB"` and you will see that the `Specification` is executed.

You can also exclude scopes using the `-Dspock.excludedScopes` parameter. If you run `mvn test -Dspock.scopes="FeatureC"` again everything will be executed. If you use `-Dspock.scopes="FeatureA"` *or* `-Dspock.scopes="FeatureB"` *everything* will be ignored for a `Specification` is to be ignored if any of its scopes is excluded.

You can also combine including and excluding scopes. In general exclusion will win over inclusion.

Scoped Features
---------------

In [ScopedFeaturesSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedFeaturesSpec.groovy) feature methods are annotated: one is scoped to "FeatureA", one to "FeatureB" and "FeatureC.

Using `-Dspock.scopes="FeatureA"` will only execute the first feature method, `-Dspock.scopes="FeatureB"` or `-Dspock.scopes="FeatureC"` only the second one.

Of course exclusion works for annotated feature as well as for `Specification`s

Combination
-----------

Combining both annotations will work as well, but it be careful: if the `Specification` get ignored, all of its features will be ignored with it no matter what they are scoped for.

See for example [ScopedBothSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedBothSpec.groovy). Its `Specification` is scoped to "FeatureA", just like its first feature method. The second feature method is scoped to "FeatureB" and "FeatureC".

If your scope does not include "FeatureA" *all* feature of the `Specification` will be ignored, even if "FeatureB" or "FeatureC" are included!

Config File
-----------

If you use an IDE it is quite inconviniet to use `-D` parameters. For this case you can use [SpockScopeConfig.groovy](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/resources/SpockScopeConfig.groovy) to set your scope.

As you can see you can use `includedScopes` just like `-Dspock.scopes` and `excludedScopes` like `-Dspock.excludedScopes`.

Note that if the corresponding `-D` parameter is used, the config file parameter gets ignored. 