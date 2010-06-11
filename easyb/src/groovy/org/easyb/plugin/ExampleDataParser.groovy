package org.easyb.plugin


public interface ExampleDataParser {
  /**
   * processData returns a Result interface if we will process this data object (whatever it is)
   *
   * @param data - the data object passed in the Story
   * @param closure - a closure that we can iterate over for each record
   * @param binding - the current binding
   *
   * @return true if we processed it, false if not
   */
  boolean processData(data, closure, binding);
}