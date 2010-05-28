package org.easyb

import java.util.regex.Pattern
import org.easyb.listener.ExecutionListener
import org.easyb.result.Result
import org.easyb.delegates.EnsuringDelegate
import org.easyb.delegates.PlugableDelegate
import org.easyb.util.BehaviorStepType

/**
 * story keywords just collects and parses the data into steps inside contexts
 */
class StoryKeywords extends BehaviorKeywords {
  def scenariosRun = false

  StoryContext topContext
  StoryContext currentContext
  BehaviorStep currentStep

  StoryKeywords(ExecutionListener listener) {
    super(listener)

    topContext = new StoryContext()
    currentContext = topContext
  }

  def examples(description, data, closure) {
    def step = new BehaviorStep(BehaviorStepType.EXAMPLES, description, closure)

    // if a closure has been passed, we need to evaluate the closure within the context of a new story context
    if (closure != null) {
      StoryContext ctx = new StoryContext()

      StoryContext oldContext = currentContext
      oldContext.childContexts.add(ctx)
      currentContext = ctx

      currentContext.exampleData = data
      currentContext.exampleStep = step

      try {
        // all new scenarios, etc go into this closure
        closure()
      } finally {
        currentContext = oldContext
      }
    } else {
      currentContext.exampleData = data
      currentContext.exampleStep = step
    }
  }

  /**
   * sharedBehavior - this is just a scenario that gets reused and not specifically run.
   *
   * @param description
   * @param closure
   * @return
   */
  def sharedBehavior(description, closure) {
    parseScenario(closure, description, BehaviorStepType.SHARED_BEHAVIOR)
  }

  def itBehavesAs(description) {
    addStep(BehaviorStepType.BEHAVES_AS, description, null)
  }

  def pendingClosure = {
    listener.gotResult(new Result(Result.PENDING))
  }

  def before(description, closure) {
    currentContext.beforeScenarios = parseScenario(closure, description, BehaviorStepType.BEFORE)
  }

  def after(description, closure) {
    currentContext.afterScenarios = parseScenario(closure, description, BehaviorStepType.AFTER)
  }

  def beforeEach(description, closure) {
    currentContext.beforeEach = parseScenario(closure, description, BehaviorStepType.BEFORE_EACH)
  }

  def afterEach(description, closure) {
    currentContext.afterEach = parseScenario(closure, description, BehaviorStepType.AFTER_EACH)
  }

  def scenario(scenarioDescription, scenarioClosure) {
    println "scenario ${scenarioDescription}"
    parseScenario(scenarioClosure, scenarioDescription, BehaviorStepType.SCENARIO)
  }

  def parseScenario(scenarioClosure, scenarioDescription, BehaviorStepType type) {
    println "runScenario"
    def scenarioStep = new BehaviorStep(type, scenarioDescription, scenarioClosure)

    currentStep = scenarioStep

    if (currentContext.ignoreAll || currentContext.ignoreList.contains(scenarioDescription)
        || currentContext.ignoreRegEx?.matcher(scenarioDescription)?.matches()) {
      scenarioStep.ignore = true
    } else if (scenarioClosure == pendingClosure) {
      scenarioStep.pending = true
    } else {
      currentContext.addStep(scenarioStep)
      scenarioClosure() // now parse the scenario
    }

    currentStep = null

    return scenarioStep
  }

  def addPlugin(plugin) {
    currentContext.addPlugin plugin
  }

  def replaySteps(executeStory, binding) {
    // this allows the user to run the scenarios before the end of the script if they wish
    if (scenariosRun)
      return

    StoryContext.binding = binding

    scenariosRun = true

    println "running"

    StoryProcessing sp = new StoryProcessing()
    sp.processStory(topContext, executeStory, listener)
  }


  private def addStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    BehaviorStep step = new BehaviorStep(inStepType, inStepName, closure)

    if (closure == pendingClosure)
      step.pending = true

    if (!currentStep)
      currentContext.addStep step
    else
      currentStep.addChildStep step
  }

  private def addPlugableStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    if (closure != null && closure != pendingClosure)
      closure.delegate = new PlugableDelegate()

    addStep(inStepType, inStepName, closure)
  }

  private def addEnsuringStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    if (closure != null && closure != pendingClosure)
      closure.delegate = new EnsuringDelegate()

    addStep(inStepType, inStepName, closure)
  }

  def given(givenDescription, closure) {
    addPlugableStep(BehaviorStepType.GIVEN, givenDescription, closure)
  }

  def when(whenDescription, closure = {}) {
    addPlugableStep(BehaviorStepType.WHEN, whenDescription, closure)
  }

  def then(spec, closure) {
    addEnsuringStep(BehaviorStepType.THEN, spec, closure)
  }

  def and(description, closure) {
    if (currentStep.lastChildsBehaviorStepType == BehaviorStepType.GIVEN) {
      given(description, closure)
    }
    else if (currentStep.lastChildsBehaviorStepType == BehaviorStepType.WHEN) {
      when(description, closure)
    }
    else if (currentStep.lastChildsBehaviorStepType == BehaviorStepType.THEN) {
      then(description, closure)
    }
  }

  def but(description, closure) {
    and(description, closure)
  }

  def ignoreOn() {
    currentContext.ignoreAll = true
  }

  def ignoreOff() {
    currentContext.ignoreAll = false
  }

  def ignore(scenarios) {
    if (!currentContext.ignoreAll) {
      currentContext.ignoreList = scenarios
    }
  }

  def ignore(Pattern scenarioPattern) {
    if (!currentContext.ignoreAll) {
      currentContext.ignoreRegEx = scenarioPattern
    }
  }

}