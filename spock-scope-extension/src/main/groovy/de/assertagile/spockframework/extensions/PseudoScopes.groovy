package de.assertagile.spockframework.extensions

interface PseudoScopes {

    interface Unscoped extends SpecScope {}

    interface AllBut extends SpecScope {}
}
