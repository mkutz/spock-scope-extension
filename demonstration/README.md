Demonstration
=============

This folder contains an example project demonstrating how to use the [Spock Scope Extension](https://github.com/mkutz/spock-scope-extension).

Scoped Specification
--------------------

You can use the `@Scope` annotation to scope an entire `Specification`. A scoped spec will always be executed as long as there is no scope set at all or at least one of its scopes is included.

As you can see the [ScopedSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedSpec.groovy) is annotated with `@Scope` and a list of scopes `FeatureA` and `FeatureB` .

The scopes are defined in the [SpecScopes](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/SpecScopes.groovy) class. Note that all scope classes you want to use must implement the SpecScope interface.

Try to execute
```
mvn test -Dspock.scopes="FeatureC" -Dtest=ScopedSpec
```
As [ScopedSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedSpec.groovy) is not scoped for `FeatureC` it will be ignroed completly.

Try `-Dspock.scopes="FeatureA"`, `-Dspock.scopes="FeatureB"`, `-Dspock.scopes="FeatureA, FeatureB"` or even  `-Dspock.scopes="FeatureA, FeatureC"` and you will see that the `Specification` is executed.


You can also exclude scopes using the `-Dspock.excludedScopes` parameter. If you run `mvn test -Dspock.scopes="FeatureC"` again everything will be executed. If you use `-Dspock.scopes="FeatureA"` *or* `-Dspock.scopes="FeatureB"` *everything* will be ignored for a `Specification` is to be ignored if any of its scopes is excluded.

You can also combine including and excluding scopes. In general exclusion will win over inclusion.

Scoped Features
---------------

You can as well use the `@Scope` annotation to scope a single feature method within a `Specification`. The resultung behavior is the same as for `Specification`s.

In [ScopedFeaturesSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedFeaturesSpec.groovy) feature methods are annotated: one is scoped to `FeatureA`, one to `FeatureB` and `FeatureC`. A third feature is not scoped at all.

Try to run
```
mvn test -Dspock.scopes="FeatureA" -Dtest=ScopedFeaturesSpec
```
As the first feature method is the only one scoped to `FeatureA` it is the only one being executed.

Try `-Dspock.scopes="FeatureB"` or `-Dspock.scopes="FeatureC"` to execute the second feature method. To execute both beatures you can use for example `-Dspock.scopes="FeatureA,FeatureC"`.

The third feature method will only be executed if no scope is set at all:
```
mvn test -Dtest=ScopedFeaturesSpec
```

Combination
-----------

Combining both annotations will work as well, but it be careful: if the `Specification` gets ignored, all of its features will be ignored with it no matter what they are scoped for. So combining both is generally not what you want.

In [ScopedBothSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedBothSpec.groovy) the `Specification` is scoped to `FeatureA`, just like its first feature method. The second feature method is scoped to "FeatureB" and "FeatureC".

If your scope does not include "FeatureA" *all* feature methods of the `Specification` will be ignored, even if "FeatureB" or "FeatureC" are included!

For example
```
mvn test -Dspock.scopes="FeatureC" -Dtest=ScopedFeaturesSpec
```
will ignore the entire `Specification` including the second feature method, which is scoped to `FeatureC`. Using `-Dspock.scopes="FeatureA"` will only execute the first feature method.

If you combine both annotations, you might want the feature methods' scoped to include all of the `Specification` scopes.

Unscoped
--------

If you set a scope for your execution, all unscoped feature methods will genrally be ignored.

Look into [UnscopedSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/UnscopedSpec.groovy): it has no `@Scope` at all.

Running
```
mvn test -Dspock.scopes="FeatureC" -Dtest=UnscopedSpec
```
will ignore the entire `Specification` just like it was scoped to an unselected scope.

If will only be executed if you don't set any scope:
```
mvn test -Dtest=UnscopedSpec
```

Excluding Scopes
----------------

You can as well exclude a scope. So if you have a feature that is not relevant for your test run, you can just skip all tests regarding it.

For example if you run [ScopedFeaturesSpec](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/groovy/de/assertagile/demonstration/ScopedFeaturesSpec.groovy) with `FeatureA` excluded, it will only execute the second feature:
```
mvn test -Dspock.excludedScopes="FeatureA" -Dtest=ScopedFeaturesSpec
```
The unscoped feature method is being ignored just like those scoped to the excluded scope.

Config File
-----------

If you use an IDE it is quite inconviniet to use `-D` parameters. For this case you can create a file `SpockScopeConfig_example.groovy` in you project's resouce path (e.g. `src/test/resources`) to set your scope.

As you can see in [SpockScopeConfig_example.groovy](https://github.com/mkutz/spock-scope-extension/blob/master/demonstration/src/test/resources/SpockScopeConfig.groovy) you can use `includedScopes` just like `-Dspock.scopes` and `excludedScopes` like `-Dspock.excludedScopes`.

Note that if the corresponding `-D` parameter is used, the config file parameter gets ignored, so checking in the file won't influence your scoped CI runs *but* it will be used if you don't set a scope. So `mvn clean install` will be scoped! So generally you don't want the file to be checked in!
