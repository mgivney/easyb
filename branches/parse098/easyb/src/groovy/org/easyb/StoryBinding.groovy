package org.easyb

import org.easyb.StoryKeywords
import org.easyb.listener.ExecutionListener
import org.easyb.plugin.EasybPlugin
import org.easyb.plugin.NullPlugin
import org.easyb.plugin.PluginLocator

class StoryBinding extends Binding {
  StoryKeywords story
  def activePlugins = []

  public replaySteps(boolean executeStory) {
    story.replaySteps(activePlugins, executeStory, this)
  }

  def StoryBinding(ExecutionListener listener) {
    this.story = new StoryKeywords(listener)
    this.activePlugins = activePlugins

    using = {pluginName ->
      plugin = new PluginLocator().findPluginWithName(pluginName)

      if ( !activePlugins.contains(plugin) ) {
        activePlugins << plugin
      }
    }

    before = {description = "", closure = {} ->
      story.before(description, closure)
    }

    assumption = before.curry("story assumption")
    assuming = before.curry("story assumes")

    after = {description = "", closure = {} ->
      story.after(description, closure)
    }

    before_each = {description = "", closure = {} ->
      story.beforeEach(description, closure)
    }

    after_each = {description = "", closure = {} ->
      story.afterEach(description, closure)
    }

    scenario = {description, closure = story.pendingClosure ->
      story.scenario(description, closure)
    }

    runScenarios = { ->
      println "running story"
      story.replaySteps(activePlugins, true, this)
    }

    then = {spec, closure = story.pendingClosure ->
      story.then(spec, closure)
    }

    when = {description, closure = {} ->
      story.when(description, closure)
    }

    given = {description, closure = {} ->
      story.given(description, closure)
    }

    and = {description = "", closure = story.pendingClosure ->
      story.and(description, closure)
    }

    but = {description = "", closure = story.pendingClosure ->
      story.but(description, closure)
    }

    narrative = {description = "", closure = {} ->
      story.narrative(description, closure)
    }

    description = {description ->
      story.description description
    }

    easybResults = {
      story.easybResults()
    }

    all = {
      story.ignoreOn()
    }

    tags = {
      //nop
    }

       
    ignore = {Object ... scenarios ->
      if (scenarios.size() == 1) {
        def objscn = scenarios[0]

        if ( objscn instanceof String )
          story.ignore(objscn)
        else if ( objscn == all || !(objscn instanceof Closure) ) {
          try {
            objscn.call()
          } catch (excep) {
            if (scenarios[0].getClass() == String) {
              story.ignore([scenarios[0]])
            } else {
              story.ignore(scenarios[0])
            }
          }
        } else if ( objscn instanceof Closure ) {
          story.ignoreOn()

          try {
            objscn.call()
          } finally {
            story.ignoreOff()
          }
        }
      } else if (scenarios.size() > 1) {
        story.ignore(scenarios as List)
      } else {
        //no argument but this isn't working
      }
    }

    shared_behavior = {description, closure = {} ->
      story.sharedBehavior(description, closure)
    }

    it_behaves_as = {description ->
      story.itBehavesAs(description)
    }
  }

  def getAt(ArrayList list) {
    this.story.ignoreList = list
  }

  /**
   * This method returns a fully initialized Binding object (or context) that
   * has definitions for methods such as "when" and "given", which are used
   * in the context of stories.
   */
  static StoryBinding getBinding(listener) {
    return new StoryBinding(listener)
  }
}
