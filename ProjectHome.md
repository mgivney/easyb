easyb is a BDD framework that provides a domain specific language which makes application verification simple, fun, and easy. easyb provides support for two types of specifications, Behaviors and Stories. Behaviors are created with a simple `it` syntax. Stories are created with `scenario`, `given`, `when` and `then`.

Curious? Check out this video, which demonstrates both specifications and stories in easyb.



wiki:video: cannot find YouTube video id within parameter "url".



## news ##
  * [easyb 1.2](http://repo1.maven.org/maven2/org/easyb/easyb-core/1.2/) has been released (May 2011) - all bundles are now on maven central for easy download for dependency managers. What was easyb is being split into a number of different artifacts to allow it to be easy to add functionality to.
  * [easyb 0.9.8](http://easyb.googlecode.com/files/easyb-0.9.8.tar.gz) has been released! (October 2010)
  * Check out John Ferguson Smart's [Acceptance-TDD with easyb](http://weblogs.java.net/blog/johnsmart/archive/2010/02/12/acceptance-test-driven-development-bring-developers-and-testers-to) video! (February 2010)
  * New IntelliJ plugin (v0.9.6) via IntelliJ's Plugin Manager (January 2010)
  * Check out [easyb-junit](http://code.google.com/p/easyb-junit/), which enables [running easyb via JUnit](http://code.google.com/p/easyb/wiki/UsingEasybWithJUnit) (December 2009)
  * Version 0.9.6 of Maven plugin is now available at easyb Maven repos & Maven central repo (December 2009)
  * [XMLUnit plug-in](http://code.google.com/p/easyb/wiki/XMLUnitPlugIn) is now available. (November 2009)
  * easyb [now has an Eclipse plug-in](http://code.google.com/p/easyb/wiki/InstallingEclipsePlugin). (October 2009)
  * The easyb project is on [Twitter](http://www.twitter.com/easybdd)! (May 2009)
  * easyb is a [Jolt Award winner](http://www.joltawards.com/winners.html)! (March 2009)


## easyb in action ##

easyb enables you to verify behavior of normal Java objects, work-flows, etc (basically, anything you write in Java) in a more natural way-- for instance, imagine having a conversation with a customer who wants you to write something to validate zip codes.

> "Could you please write something that lets my customers know when they've provided an invalid zip code?"

> "Sure! So, given an invalid zip code, this validation service should notify someone that the zip code is incorrect?"

> "Right on! Man, you are smart!"

Notice that nowhere in this conversation did anyone say test! Whether or not you write specifications first or afterwards is up to you; however, assuming that you've already written the zip code validator, you could construct a story like so:

  * Given that someone mistypes a zip code
  * And given the zip code validation service is up and running
  * When validate is invoked
  * Then the service should indicate the zip code is invalid

Using the text above, you can then construct an easyb story like so:

```
given "an invalid zip code", {
 invalidzipcode = "221o1"
}

and "given the zipcodevalidator is initialized", {
 zipvalidate = new ZipCodeValidator()
}

when "validate is invoked with the invalid zip code", {
 value = zipvalidate.validate(invalidzipcode)
}

then "the validator instance should return false", {
 value.shouldBe false
}
```

Is that easy or what? Notice the `shouldBe` call in the `then` phrase-- slick, eh?

BDD in Java can't get any easier!

For more information on easyb, check out [easyb.org](http://www.easyb.org) where you can find more examples, a tutorial, how to run easyb and much, much more!