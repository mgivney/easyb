# Introduction #

[Gaelyk](http://gaelyk.appspot.com/) is a lightweight web application framework for Google App Engine. As Gaelyk's documentation states: "Gaelyk simplifies the usage of the Google App Engine SDK by providing more concise and more powerful shortcuts when using the datastore, memcache, the blobstore, the images service, the URL fetch service, when sending and receiving emails or Jabber messages, and much more."

Thus, the easyb-gaelyk plugin makes verifying Gaelyk applications all the more easier by providing a context where Gaelyk's `GaelykCategory` is in play.


# Details #

To use the plugin, simply put a `using "gaelyk"` phrase in your easyb story. Note, this plugin, as of now, must be used in coordination with easyb's GAE plugin (as Gaelyk and most likely, your stories, will require Google's underlying services running locally), thus, you'll need two `using` phrases:
```
using "GAE"
using "gaelyk"
```

Any story that issues a `using "gaelyk"`  phrase will have the ability to issue any of Gaelyk's nifty auto-magical methods attached to various objects, such as Google's `Entity` object. For example, the following story demonstrates the easyb-gaelyk plugin in action:

```
import com.google.appengine.api.datastore.Entity
import com.google.appengine.api.datastore.Query
import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit

using "GAE"
using "gaelyk"

scenario "Gaelyk magic should work as normal", {
 given "params map i.e. form inputs", {
   params = [:]
   params.name = "Andy"
   params.type = "Facebook"
 }

 then "an account low-level entity should be created", {
   acct = new Entity("account")
   acct.name = params.name
   acct.type = params.type
   acct.save()		  
 }
 
 and "one can find it easily and it should act Gaelyk-y", {
   qry = new Query("account")
   qry.addSort("name", Query.SortDirection.DESCENDING)
   prp = datastore.prepare(qry)
   accts = prp.asList(withLimit(1))
   accts[0].class.shouldBe Entity
   accts[0].type.shouldBe "Facebook"
 }
}
```

For more information regarding Gaelyk, see [their website](http://gaelyk.appspot.com/) and you can find the easyb-gaelyk plugin on [easyb's download site](http://code.google.com/p/easyb/downloads/list).