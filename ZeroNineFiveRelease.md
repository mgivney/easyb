# What's new #

At a high level, this version includes:

  * HTML reports
    * Just pass in the -html flag via the command line or specify the reporting format as html via the Ant/Maven task
  * Enhanced exception reporting
    * easyb offers two options:
      * -e for a full exception stack trace
      * -ef for exception stack trace filtering — that is, a lot of the noise associated with a Groovy exception stack is filtered out
  * Support for ignoring scenarios
    * ignore is a hip keyword now in stories and it can take as an argument a scenario name, a list of scenario names, and even a regular expression
  * Pretty printing
    * Pass in the -prettyprint flag and the output will be nicely colored, baby!
  * Narratives support non underscores
    * as\_a can alternately be written as as a along with i want and so that
  * Numerous API improvements along with bug fixes!
    * For instance, the ensureThrows takes a list of exceptions now
  * This version leverages Groovy 1.6.0

[download it now](http://easyb.googlecode.com/files/easyb-0.9.5.tar.gz)!