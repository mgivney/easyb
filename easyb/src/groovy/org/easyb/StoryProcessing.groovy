package org.easyb

import java.util.regex.Pattern
import org.easyb.listener.ExecutionListener
import org.easyb.result.Result
import org.easyb.delegates.EnsuringDelegate
import org.easyb.delegates.PlugableDelegate
import org.easyb.util.BehaviorStepType

public class StoryProcessing {
  StoryContext currentContext
  boolean executeStory
  ExecutionListener listener

  public def processStory(StoryContext currentContext, boolean executeStory, ExecutionListener listener){
    this.currentContext = currentContext
    this.executeStory = executeStory
    this.listener = listener

    runContext(currentContext)
  }

  /*
    runs all of the scenarios, befores and afters in this context
   */
  def runContext(StoryContext context) {
    // let the plugins know we are starting
    context.notifyPlugins { plugin, binding ->
      plugin.setClassLoader(getClass().getClassLoader())
      plugin.beforeStory(binding)
    }

    if (context.beforeScenarios)
      processScenario(context.beforeScenarios, false)

    try {
      context.scenarioSteps.each { BehaviorStep step ->
        if (step.stepType == BehaviorStepType.SCENARIO) {
          if ( step.ignore ) {
            listener.startStep step
            listener.gotResult new Result(Result.IGNORED)
            listener.stopStep()
          } else if ( step.pending ) {
            listener.startStep step
            step.closure()
            listener.stopStep()
          } else {
            processScenario step, true
          }
        } else {
          processChildStep step
        }
      }

    } finally {
      if (context.afterScenarios)
        processScenario(context.afterScenarios, false)

      context.notifyPlugins { plugin, binding -> plugin.afterStory(binding) }
    }
  }


  private def processSharedScenarios(name) {
    BehaviorStep shared = currentContext.scenarios[name]

    if (!shared) { // can't find the shared scenario
      def result = new Result(Result.FAILED)
      result.description = "Unable to find shared scenario ${name}"

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

    if (childStep.pending)
      action = childStep.closure
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
          currentContext.notifyPlugins { plugin, binding -> plugin.beforeThen(binding) }
          action()
          currentContext.notifyPlugins { plugin, binding -> plugin.afterThen(binding) }
          break;
        case BehaviorStepType.WHEN:
          currentContext.notifyPlugins { plugin, binding -> plugin.beforeWhen(binding) }
          action()
          currentContext.notifyPlugins { plugin, binding -> plugin.afterWhen(binding) }
          break;
        case BehaviorStepType.GIVEN:
          currentContext.notifyPlugins { plugin, binding -> plugin.beforeGiven(binding) }
          action()
          currentContext.notifyPlugins { plugin, binding -> plugin.afterGiven(binding) }
          break;
      }


    } catch (Throwable t) { // who knows what could happen, whatever does, its a failure
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
        currentContext.notifyPlugins { plugin, binding -> plugin.beforeScenario(binding) }

      if (isRealScenario && currentContext.beforeEach)
        processScenario(currentContext.beforeEach, false)

      step.childSteps.each { childStep ->
        println "childStep ${childStep.stepType} ${childStep.name}"
        if (childStep.stepType == BehaviorStepType.BEHAVES_AS)
          processSharedScenarios(childStep.name)
        else if (childStep.closure && processing.contains(childStep.stepType)) {
          processChildStep(childStep)
        }
      }

      if (step.childStepFailureResultCount == 0)
        listener.gotResult(new Result(Result.SUCCEEDED))

      if (isRealScenario && currentContext.afterEach)
        processScenario(currentContext.afterEach, false)

    } finally {
      listener.stopStep()

      if (isRealScenario)
        currentContext.notifyPlugins { plugin, binding -> plugin.afterScenario(binding) }
    }
  }
}