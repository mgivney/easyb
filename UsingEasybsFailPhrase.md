Occasionally during the course of writing an easyb story or specification, you might run into a condition that requires a forced failure. That is, based upon some behavior of the code under verification, you might explicitly want easyb to fail a particular scenario. For example, below is a `then` phrase within a scenario that contains a conditional — if something is true then verify some result; however, if something is false, then force a failure:

```
then "the cell returned should be a date type", {
 sndcells = sheet.getRow(1)
 dtype = sndcells[2].getType()
 if(dtype == CellType.DATE){
  dt = sndcells[2].getDate()
  dt.getTime().shouldBe 1201737600000
 }else{
  fail "the type obtained wasn't a date, but was a ${dtype}"
 }
}
```

The code above (which is a snippet of a larger story on parsing an Excel template) verifies that a `Date` type is obtained from a particular cell represented as a string (i.e. 1/31/2009). If the `dtype` variable is of a desired type (i.e. `Date`), one can easily validate it via the `shouldBe` phrase. If for some reason, however, the cell Iisn’t a date, you can force easyb to fail by using the `fail` phrase, which takes a `String`.