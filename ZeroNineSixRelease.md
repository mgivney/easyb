# What's new #

At a high level, this version includes:

  * Re-running only failed behaviors
    * easyb will create an out file listing failed behaviors via the `-outfail` option
    * Just pass in the `-f` flag via the command line (see next bullet) to run those failures
  * Running behaviors listed in a file
    * Pass in the `-f` flag pointing to a file
  * Running behaviors in parallel (via the `-parallel` option)
  * `ensureUntil` supports the notion of a timeout (see [issue 146](http://code.google.com/p/easyb/issues/detail?id=146))
  * Ant task updates to support new command line features
  * [shared behaviors](http://code.google.com/p/easyb/wiki/SharedBehaviors)
  * This version leverages Groovy 1.6.4

[download it now](http://easyb.googlecode.com/files/easyb-0.9.6.tar.gz)!