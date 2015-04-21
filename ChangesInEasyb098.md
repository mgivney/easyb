# Introduction #

There is a fundamental change in how easyb works in 0.9.8 in relation to **stories** (not specifications). Easyb stories are now parsed and then run which enables a few things, primarily better structure around before/after and fitness style testing. Additional support for Gherkin and Concordian like tests are in the pipeline as well.

Other things are being added by other people and this list is maintained in a separate wiki page.

**This is not an "official" build of easyb 0.9.8, it is just a place to put it for those who are interested in working with functionality as it becomes available**.

# Availability #

Builds of Easyb 0.9.8 are being released in stages as functionality is slowly being added. This allows people to test their scripts against the new functionality - but be aware that if you delve into the internals too much, you may find them changing on you. These updates are available in the maven repository in http://code.google.com/p/easyb/source/browse/#svn/maven/maven2 - wagon is being a bit temperamental about ensuring the latest release is really the latest release so manually poking around now is the best idea. Once this is fixed, this document will update and your ivy or maven dependency range should look like this: [0.9.8.0, 0.9.8.999)

  * 0.9.8.1 - this adds a few new capabilities to easyb
    * all of the functionality around the where/examples clause that brings fitness style data to easyb. The api is extensible with your own plugins and two plugins are available that process data.
    * A side effect of the where/examples clause is variable replacement.
    * _Multiple plugins per Easyb script are now available_ (there is no limit to the number that are available). They can also have a second parameter that creates a variable in the binding of that type.
  * 0.9.8.2 - this sorts out the issue around parallel reports (which ended up largely being unusable)

# migrating from easyb 0.9.7 #

The primary thing that could trip you up is the parse/run separation. Until the entire file is parsed and turned into an execution tree, it will not start processing. If you depend in the sequential script like behavior you can issue the command:
```
runScenarios()
```
in your script at which point execution will start as if easyb itself was doing it. Anything after that point will be sequential (but further story definitions will be ignored).

# variable replacement #

If variables are in context at the time your story is running (e.g. they are in a before, before\_each or where (see below)), you can use them in the text of your story. Because of the way Groovy works, the $ symbol is not usable, and has been replaced with # (to be consistent with Spock). To forms are available:

  * "#number is given" - this uses the simple form. If a value for 'number' exists in the binding at the time (say 5), this will appear as "5 is given"
  * "#total should be #{val1 + val2}" - the closure form creates a (reused) Groovy script which is passed the binding and can thus have any Groovy code in it. It is slower than the first option.

# where/example clause #

Easyb 0.9.8.1 adds in the example or where clause to your tests. It does this by adding a "context" within which one or more scenarios will run - a context provides:

  * data: _optional_ - if data is provided the default behavior is to cycle around the source of data running the scenarios contained within the context until the data is exhausted
  * before/after scenarios: _optional_ - operate like they do now, but there can be one set for each context
  * before\_each and after\_each: _optional_ - as before/after
  * one or more stories: as existing

## Syntax ##

```
where "description", data[, {]
 
  scenarios...

}]
```
a where clause can exist inside a single scenario, e.g

```
scenario "scenario", {
  given ""
  when ""
  then ""
  where "", data
}
```

(order does not matter)

a where clause can be multi level

```
where "description", data, {
  scenarios...

  where "description", data2, {
    scenarios

    scenario "", {
      then ""
      where ""
    }
  }
}
```

To as many levels as you wish.

## Shared scenarios ##


Shared scenarios that exist in one context are available to all contexts underneath them.

## Example ##
```
package org.easyb.where

/*
Example tests a map at the story level
 */

numberArray = [12, 8, 20, 199]

where "we are using sample data at a global level", [number:numberArray]

before "Before we start running the examples", {
  given "an initial value for counters", {
    println "initial"
    whenCount = 0
    thenCount = 0
    numberTotal = 0
  }
}

scenario "Number is #number and multiplier is #multiplier and total is #{number * multiplier}", {
  when "we multiply #number by #multiplier", {
    whenCount ++
    num = number * multiplier
  }
  then "our calculation (#num) should equal #{number * multiplier}", {
    num.shouldBeGreaterThan 0
    numberTotal += num
    thenCount ++
  }
  where "Multipliers should be", {
    multiplier = [1,2,3]
  }
}


after "should be true after running example data", {
  then "we should have set totals", {
    whenCount.shouldBe 12
    thenCount.shouldBe 12
    num = 0
    numberArray.each { n ->
      num = num + (n + (2*n) + (3*n))
    }

    num.shouldBe numberTotal
  }
}
```

## sources of data ##
The way that data is processed in the where clause depends on plugins and two plugins are added by default to the **end** of the list of plugins defined for dealing with where _data_. These deal with a map or a closure - both examples are shown above. The syntax for these plugins will be changing to deal with Gherkin. The current interface that needs to be implemented is:

```
public interface ExampleDataParser {
  /**
   * processData if the data is of a type that this plugin should parse, then
   * it needs to decode it, calling the closure for each iteration with a map
   * containing fields and values to add to the binding.  The parser can add new elements
   * to the binding for the duration of its operation, but must remove them (for consistency)
   * on completion. The StoryProcessing will take care of removing the excess properties
   * in the binding that occur because of these plugins.
   *
   * @param data - the data object passed in the Story
   * @param closure - a closure that we can iterate over for each record. It expects one parameter { map -> }
   * @param binding - the current binding
   *
   * @return true if we processed it, false if not
   */
  boolean processData(data, closure, binding);
}
```