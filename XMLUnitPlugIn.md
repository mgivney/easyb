# Using the XMLUnit easyb plug-in #

There’s myriad ways to validate XML; in fact, with Groovy, the mechanics of parsing XML with `XMLSlurper` couldn’t be easier! Nevertheless, from time to time, you might have needed an easy way to validate XML documents without having to actually parse them. Thus, [XMLUnit](http://www.ibm.com/developerworks/java/library/j-cq121906.html) has been a handy framework today as it has been for years.

While XMLUnit is a JUnit-style framework, you can certainly leverage it outside of JUnit. What’s more, via easyb’s plug-in framework, using XMLUnit just got a lot easier! For instance, leveraging XMLUnit with this plug-in makes validation as easy as:

```
using "xmlunit"

scenario "XML documents are compared easier via xml unit plug-in", {

 given "some xml document", {
  control = """<account><id>3A-00</id><name>acme</name></account>"""
 }

 then "the plugin should enable easy comparisons", {
  testXML = """<account><id>3A-00</id><name>acme</name></account>"""
  testXML.shouldBeIdenticalTo control
  testXML.shouldBeIdenticalWith control  //same behavior different call
  testXML.shouldBeIdentical control      //ditto
  testXML.identical control              //ditto
 }

 and "the instance of XMLUnit should be available for use", {
  XMLUnit.version.shouldBe "1.3alpha"
  XMLUnit.getIgnoreWhitespace().shouldBe true
 }
```

Note how the `using` keyword is required -- this loads the XMLUnit easyb plug-in (providing you've placed the plug-in in your classspath). The key aspects of the  plug-in are the special validation methods, such as `shouldBeIdenticalTo` and its logical cousin `shouldBeSimilarTo`. Note too, these methods have synonyms (see the code above). What's more, you can grab an instance of the core XMLUnit framework as it's automatically present in the binding available to your behaviors (as `XMLUnit`). Lastly, all `should` methods in easyb support an optional message parameter that will be captured if there is an error -- `testXML.shouldBeIdenticalTo control, "documents were not identical"` -- as you can see this is quite similar to JUnit-style assertions.


If you should have any questions, please feel free to post your question on <a href='http://groups.google.com/group/easyb-users'>the users list</a> or you can <a href='http://twitter.com/easybdd'>twitter easyb</a>.
