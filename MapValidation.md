easyb's `should` syntax is quite flexible and supports a number of combination's for verifying map contents; for example, imagine a map defined as so (note, I'll define the map in Groovy; however, I could have easily done the same thing in Java -- there is no difference between the two):

```
def namemap = ["WKL_ID":"id", "NBS": "cst", "EFF_DT":"effectiveDate"]
```

easyb supports a `shouldHave` call on instances of collections; therefore, I can easily write the following checks:
```
namemap.shouldHave "NBS"
namemap.shouldHave "WKL_ID":"id"
namemap.shouldHave "effectiveDate"
```
As you can see, with the `shouldHave` call, you can verify keys and values; what's more, you can even validate the presence of a name-value pair.