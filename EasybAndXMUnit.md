Ever find yourself needing to validate XML? If so, you might want to consider using [XMLUnit](http://xmlunit.sourceforge.net/). XMLUnit is a JUnit extension framework that facilitates testing XML documents — you can use it to validate the structure of a document, its contents, and even distinct portions of the document.

While easyb might look and feel quite differently than good old JUnit, there is nothing to stop you from leveraging existing frameworks (like XMLUnit and Selenium, for example), which were originally built targeting JUnit. That is to say, baby, all those libraries that have been around for years helping developers validate code are usable in easyb!

Watch:

```
import org.custommonkey.xmlunit.XMLUnit
import org.custommonkey.xmlunit.Diff

XMLUnit.setIgnoreWhitespace(true)

scenario "the XMLRepresentationBuilder should build valid XML", {

 given "a table name and collection of name value pairs", {
  resname = "currentComnWidgRes"
  lst = [new ColumnNameValue("WGT_ID", 10002130),
   new ColumnNameValue("NISB", "99.99"),
   new ColumnNameValue("EFF_DT", "2009-04-01")]
 }

 then "the XML produced should be valid", {
  out = XMLRepresentationBuilder.buildRepresentation(resname, lst)
  control = """<currentComnWidgRes id='10002130'>
                 <NISB>99.99</NISB><EFF_DT>2009-04-01</EFF_DT>
              </currentComnWidgRes>"""
  diff = new Diff(control, out)
  diff.identical().shouldBe true
 }
}
```

As you can see in the code above, this easyb story uses XMLUnit to compare the output (using XMLUnit’s `Diff` class) of the
`XMLRepresentationBuilder` class, which builds an XML document from a model. The resulting document is compared to a control document (i.e. the `control` variable).