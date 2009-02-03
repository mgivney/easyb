package org.disco.bdd.specification.stringutil

import org.disco.bdd.StringUtilPartial

it "should return null for null input to trim", {
    StringUtilPartial.trim(null).shouldBe null
}

it "should return the string without whitespace at either the end", {
    StringUtilPartial.trim(" somestring ").shouldNotBe null
    StringUtilPartial.trim(" somestring ").shouldBe "somestring"
}
