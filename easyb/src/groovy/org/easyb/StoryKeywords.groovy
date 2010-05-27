package org.easyb

import java.util.regex.Pattern
import org.easyb.listener.ExecutionListener
import org.easyb.result.Result
import org.easyb.delegates.EnsuringDelegate
import org.easyb.delegates.PlugableDelegate
import org.easyb.util.BehaviorStepType

class StoryKeywords extends BehaviorKeywords {
  protected ArrayList<BehaviorStep> scenarioSteps = new ArrayList<BehaviorStep>()
  protected HashMap<String, BehaviorStep> scenarios = new HashMap<String, BehaviorStep>()
  private BehaviorStep currentStep

  BehaviorStep beforeScenarios
  BehaviorStep afterScenarios

  def ignoreAll = false
  def ignoreList = []
  def ignoreRegEx

  BehaviorStep beforeEach
  BehaviorStep afterEach

  def scenariosRun = false
  def executeStory = true

  def activePlugins
  def binding


  StoryKeywords(ExecutionListener listener) {
    super(listener)
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
    beforeScenarios = parseScenario(closure, description, BehaviorStepType.BEFORE)
  }

  def after(description, closure) {
    afterScenarios = parseScenario(closure, description, BehaviorStepType.AFTER)
  }

  def beforeEach(description, closure) {
    beforeEach = parseScenario(closure, description, BehaviorStepType.BEFORE_EACH)
  }

  def afterEach(description, closure) {
    afterEach = parseScenario(closure, description, BehaviorStepType.AFTER_EACH)
  }

  def scenario(scenarioDescription, scenarioClosure) {
    println "scenario ${scenarioDescription}"
    parseScenario(scenarioClosure, scenarioDescription, BehaviorStepType.SCENARIO)
  }

  def parseScenario(scenarioClosure, scenarioDescription, BehaviorStepType type) {
    println "runScenario"
    def scenarioStep = new BehaviorStep(type, scenarioDescription, scenarioClosure)

    currentStep = scenarioStep


    if (ignoreAll || this.ignoreList.contains(scenarioDescription)
        || this.ignoreRegEx?.matcher(scenarioDescription)?.matches()) {
      println "skipping"
      listener.startStep currentStep
      listener.gotResult new Result(Result.IGNORED)
      listener.stopStep()
    } else if (scenarioClosure == pendingClosure) {
      listener.startStep currentStep
      pendingClosure()
      listener.stopStep()
    } else {
      scenarioSteps.add(currentStep)
      scenarios[scenarioDescription] = currentStep
      scenarioClosure()
    }

    currentStep = null

    return scenarioStep
  }


  def replaySteps(activePlugins, executeStory, binding) {
    // this allows the user to run the scenarios before the end of the script if they wish
    if (scenariosRun)
      return

    this.executeStory = executeStory
    this.activePlugins = activePlugins
    this.binding = binding

    scenariosRun = true

    println "running"

    // let the plugins know we are starting
    activePlugins.each { plugin ->
      plugin.setClassLoader(getClass().getClassLoader())
      plugin.beforeStory(binding)
    }

    if (beforeScenarios)
      processScenario(beforeScenarios, false)

    try {
      scenarioSteps.each { step ->
        if (step.stepType == BehaviorStepType.SCENARIO) {
          processScenario step, true
        }
      }
    } finally {
      if (afterScenarios)
        processScenario(afterScenarios, false)

      activePlugins.each { plugin -> plugin.afterStory(binding) }
    }
  }

  private def processSharedScenarios(name) {
    BehaviorStep shared = scenarios[name]
    if (!shared) { // can't find the shared scenario
      def result = new Result(Result.FAILED)
      result.description = "Unable to find shared scenario ${childStep.name}"

      return result
    } else {
      processScenario shared, false
    }

    println "out of shared, back to original"

    return null
  }

  private def processChildStep(BehaviorStep childStep) {
    listener.startStep(childStep)

    // figure out what to actually do
    def action

    if (childStep.closure == pendingClosure)
      action = { listener.gotResult(new Result(Result.PENDING)) }
    else if (!executeStory)
      action = {}
    else if (childStep.stepType == BehaviorStepType.THEN)
      action = {
        use(BehaviorCategory) {
          childStep.closure()
        }
        listener.gotResult new Result(Result.SUCCEEDED)
      }
    else // other child steps
      action = {
        childStep.closure()
        listener.gotResult new Result(Result.SUCCEEDED)
      }

    // now the plugin's methods are diffuse, so we use the action closure to keep it tidy
    try {
      switch (childStep.stepType) {
        case BehaviorStepType.THEN:
          activePlugins.each { plugin -> plugin.beforeThen(binding) }
          action()
          activePlugins.each { plugin -> plugin.afterThen(binding) }
          break;
        case BehaviorStepType.WHEN:
          activePlugins.each { plugin -> plugin.beforeWhen(binding) }
          action()
          activePlugins.each { plugin -> plugin.afterWhen(binding) }
          break;
        case BehaviorStepType.GIVEN:
          activePlugins.each { plugin -> plugin.beforeGiven(binding) }
          action()
          activePlugins.each { plugin -> plugin.afterGiven(binding) }
          break;
      }


    } catch (Throwable t) {
      listener.gotResult new Result(t)
    } finally {
      listener.stopStep()
    }
  }

  private def processScenario(BehaviorStep step, isRealScenario) {
    println "running scenario ${step.name}"

    listener.startStep(step)
    Result result;

    def processing = [BehaviorStepType.GIVEN, BehaviorStepType.WHEN, BehaviorStepType.THEN]

    try {
      if (isRealScenario)
        activePlugins.each { plugin -> plugin.beforeScenario(binding) }

      if (isRealScenario && beforeEach)
        processScenario(beforeEach, false)

      step.childSteps.each { childStep ->
        println "childStep ${childStep.stepType} ${childStep.name}"
        if (childStep.stepType == BehaviorStepType.BEHAVES_AS)
          processSharedScenarios(childStep.name)
        else if (childStep.closure && processing.contains(childStep.stepType)) {
          processChildStep(childStep)
        }
      }

      if (!result)
        listener.gotResult(new Result(Result.SUCCEEDED))

      if (isRealScenario && afterEach)
        processScenario(afterEach, false)

    } finally {
      listener.stopStep()

      if (isRealScenario)
        activePlugins.each { plugin -> plugin.afterScenario(binding) }
    }
  }

  private def addStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    BehaviorStep step = new BehaviorStep(inStepType, inStepName, closure)

    if (!currentStep)
      processChildStep(step)
    else
      currentStep.addChildStep step
  }

  private def addPlugableStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    if (closure != null)
      closure.delegate = new PlugableDelegate()

    addStep(inStepType, inStepName, closure)
  }

  private def addEnsuringStep(BehaviorStepType inStepType, String inStepName, Closure closure) {
    if (closure != null)
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
    this.ignoreAll = true
  }

  def ignoreOff() {
    this.ignoreAll = false
  }

  def ignore(scenarios) {
    if (!this.ignoreAll) {
      this.ignoreList = scenarios
    }
  }

  def ignore(Pattern scenarioPattern) {
    if (!this.ignoreAll) {
      this.ignoreRegEx = scenarioPattern
    }
  }

}