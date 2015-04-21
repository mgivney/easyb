# Introduction #

Writing plug-ins for easyb is simple and primarily involves implementing the `EasybPlugin` object (or extending an adapter object called `BasePlugin`) and declaring your plug-in using the service provider pattern defined in [Sun's jar specification](http://java.sun.com/j2se/1.3/docs/guide/jar/jar.html).


# Details #

To create a plug-in for easyb:
  * Create a class which implements the `org.disco.easyb.plugin.EasybPlugin` interface (or extend `BasePlugin`, which acts as an easy adapter)
  * Create a file name `org.disco.easyb.plugin.EasybPlugin` and ensure that it is packaged in the `META-INF/services` directory of a jar created by your build process
  * This file should contain a single line which is the fully qualified name of your plug-in implementation class

For example, as demonstrated on the [easyb user list](http://groups.google.com/group/easyb-users/browse_thread/thread/214846154fa65a48) in regards to extending the `shouldBe` syntax, one way of doing this is to create a plug-in like so:

  * Create a plug-in class like this:
```
import org.easyb.bdd.prototype.ExtendedCategories
import org.easyb.plugin.BasePlugin

class BetterBePlugin extends BasePlugin {
  public String getName() {
    return "BetterBe";
  }
  public Object beforeStory(Binding binding) {
    Object.mixin ExtendedCategories
  }
} 
```

  * Note the name "BetterBe" is defined in the `getName` method of the plug-in -- next, you'll need to create a file called `org.easyb.plugin.EasybPlugin` -- under the covers, easyb uses the `sun.misc.Service` object, which checks a classpath.
    1. In that file, put a line like so: `org.easyb.plugin.BetterBePlugin`.At run-time, easyb sees the `using` keyword and attempts to load the "BetterBe" plug-in -- if it finds it, it then calls hook methods that correspond to events like `beforeStory`, `beforeThen`, `afterWhen`, etc.
  * Finally, to use the new plug-in in a story, simply specify it via the `using` keyword like so:
```
using "BetterBe"

scenario "mixins should work normally", {
  given "a definition of a new method" , {
    var = "blah"
  }
  then "mixing it into easyb should work", {
    var.betterBe "blah"
  }
}
```