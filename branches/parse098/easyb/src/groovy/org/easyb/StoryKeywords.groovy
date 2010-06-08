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

  /**
   * we create a new example step and story context, associate the example data with it and
   * then process the closure within that context. This allows examples within examples within examples as the
   * tester requires.
   *
   * @param description
   * @param data
   * @param closure
   */
  private void processExamplesClosure(description, data, closure) {
    def step = new BehaviorStep(BehaviorStepType.EXAMPLES, description, closure, currentStep)

    StoryContext ctx = new StoryContext()

    step.storyContext = ctx
    ctx.parentContext = currentContext

    currentContext = ctx

    ctx.exampleData = data
    ctx.exampleStep = step

    // preserve the step as all subsequent syntax appears underneath the example
    BehaviorStep oldStep = currentStep
    currentStep = step

    try {
      // all new scenarios, etc go into this closure
      closure()
      ctx.parentContext.addStep(currentStep)
    } finally {
      currentContext = ctx.parentContext
      currentStep = oldStep
    }
  }

  def examples(description, data, closure) {
    if (currentStep && currentStep.stepType != BehaviorStepType.EXAMPLES && currentStep.stepType != BehaviorStepType.SCENARIO) {
      throw new IncorrectGrammarException("examples keyword were it should not exist.")
    }

    if (!currentStep) {
      if (closure) {
        // if a closure has been passed, we need to evaluate the closure within the context of a new story context
        // i.e. a new EXAMPLE step gets created
        processExamplesClosure(description, data, closure)
      } else if ( currentContext.exampleData ) {
        // this is an error if it occurs twice!
        throw new IncorrectGrammarException("An attempt has been made to specify example data twice within the same context.")
      } else { // example data for current context
        // otherwise it is example data for the current context. If top level, no problem, if in example step, also no problem
        currentContext.exampleData = data
      }
    } else if ( currentStep.stepType == BehaviorStepType.EXAMPLES ) {
      if ( !closure ) // we already have data for this story context thanks,
        throw new IncorrectGrammarException("examples keyword inside an examples closure must also pass a closure for context")
      
      processExamplesClosure(description, data, closure)
    } else { // we are in a scenario, so lets associate the content with it
      currentStep.exampleData = data
      currentStep.exampleName = description
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
//    println "parse shared behavior ${description}"
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
//    println "parsing scenario ${scenarioDescription}"
    parseScenario(scenarioClosure, scenarioDescription, BehaviorStepType.SCENARIO)
  }

  def parseScenario(scenarioClosure, scenarioDescription, BehaviorStepType type) {
//    println "parseScenario"
    def scenarioStep = new BehaviorStep(type, scenarioDescription, scenarioClosure, currentStep)

    def oldStep = currentStep
    currentStep = scenarioStep

    if (currentContext.ignoreAll || currentContext.ignoreList.contains(scenarioDescription)
        || currentContext.ignoreRegEx?.matcher(scenarioDescription)?.matches()) {
      scenarioStep.ignore = true
    } else if (scenarioClosure == pendingClosure) {
      scenarioStep.pending = true
    } else {
      if (type == BehaviorStepType.SCENARIO)
      currentContext.addStep(scenarioStep)

      if (type == BehaviorStepType.SHARED_BEHAVIOR)
      currentContext.sharedScenarios[scenarioStep.name] = scenarioStep

      scenarioClosure() // now parse the scenario
    }

    currentStep = oldStep

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

//    println "running"

    StoryProcessing sp = new StoryProcessing()
    sp.processStory(topContext, executeStory, listener)
  }


  private def addStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    BehaviorStep step = new BehaviorStep(inStepType, inStepName, closure, currentStep)

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

  def setIgnoreList(list) {
    currentContext.ignoreList = list
  }

  def ignore(Pattern scenarioPattern) {
    if (!currentContext.ignoreAll) {
      currentContext.ignoreRegEx = scenarioPattern
    }
  }

}