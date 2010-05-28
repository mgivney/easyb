

/*
Holds the context in which a whole set of stories executes. The reason this is now a class is that examples can have their
own story context in which the whole chain of before/after/before_each/after_each/shared, etc can sit
 */

package org.easyb

import org.easyb.plugin.EasybPlugin
import org.easyb.util.BehaviorStepType;


public class StoryContext {

  /**
   * all of the scenarios in this context. These get evaluated when the context gets evaluated
   */
  ArrayList<BehaviorStep> steps = new ArrayList<BehaviorStep>()

  /*
   * Allows us to find them by name for shared scenarios
   */
  HashMap<String, BehaviorStep> sharedScenarios = new HashMap<String, BehaviorStep>()

  /*
   Child contexts - this should ideally go only one level deep
   */
  ArrayList<StoryContext> childContexts = new ArrayList<StoryContext>()

  StoryContext parentContext;

  /* before step */
  BehaviorStep beforeScenarios
  /* after step */
  BehaviorStep afterScenarios

  /* local context ignores */
  def ignoreAll = false
  def ignoreList = []
  def ignoreRegEx

  BehaviorStep beforeEach
  BehaviorStep afterEach

  ArrayList<EasybPlugin> activePlugins = new ArrayList<EasybPlugin>()
  static Binding binding  // this is constant across all contexts

  /* the behavior that represents the example data, output for reporting each time we loop*/
  BehaviorStep exampleStep

  /* map or array data */
  def exampleData

  /*
  * Make sure all parent plugins get fired as well 
  */
  public def getActivePlugins() {
    def p = new ArrayList<EasybPlugin>()
    p.addAll(activePlugins)

    if ( parentContext )
      p.addAll(parentContext.activePlugins)

    return p
  }

  public void addPlugin(plugin) {
    if (!activePlugins.contains(plugin))
      activePlugins.add(plugin)
  }

  public void notifyPlugins(closure) {
    activePlugins.each { plugin -> 
      closure( plugin, binding )
    }
  }

  public void addStep(BehaviorStep step) {
    steps.add(step)

    if ( step.stepType == BehaviorStepType.SHARED_BEHAVIOR )
      sharedScenarios[step.name] = step
  }
}