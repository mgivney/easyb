package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.result.Result

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

  private def processSteps(StoryContext context) {
    context.steps.each { BehaviorStep step ->
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
  }

  private def processStepsUsingMap(StoryContext context) {
    // collect field names
    int max = 0
    def fields = []

    context.exampleData.each { key, value ->
      fields.add(key)
      
      if ( !max )
        max = value.size()
    }

    for( int idx = 0; idx < max; idx ++ ) {
      fields.each { field ->
        context.binding.setProperty field, context.exampleData[field][idx]
      }

      processSteps(context)
    }
  }

  /*
    runs all of the scenarios, befores and afters in this context
   */
  private def runContext(StoryContext context) {
    // let the plugins know we are starting
    context.notifyPlugins { plugin, binding ->
      plugin.setClassLoader(getClass().getClassLoader())
      plugin.beforeStory(binding)
    }

    if (context.beforeScenarios)
      processScenario(context.beforeScenarios, false)


    try {
      if ( !context.exampleData )
        processSteps(context)
      else if ( context.exampleData instanceof Map ) {
        if ( context.exampleData.size() ) {
          processStepsUsingMap(context)
        }
      }
    } finally {
      if (context.afterScenarios)
        processScenario(context.afterScenarios, false)

      context.notifyPlugins { plugin, binding -> plugin.afterStory(binding) }
    }
  }


  private def processSharedScenarios(name) {
    BehaviorStep shared = currentContext.sharedScenarios[name]

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
      action = { childStep.replay() }
    else if (!executeStory)
      action = {}
    else if (childStep.stepType == BehaviorStepType.THEN)
      action = {
        use(BehaviorCategory) {
//          println "runnig then closure"
          childStep.replay()
        }
        childStep.result = new Result(Result.SUCCEEDED)
        listener.gotResult childStep.result
      }
    else // other child steps
      action = {
        childStep.replay()
        childStep.result = new Result(Result.SUCCEEDED)
        listener.gotResult childStep.result
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
      childStep.result = new Result(t)
      listener.gotResult childStep.result
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
//        println "childStep ${childStep.stepType} ${childStep.name}"
        if (childStep.stepType == BehaviorStepType.BEHAVES_AS)
          processSharedScenarios(childStep.name)
        else if (childStep.closure && processing.contains(childStep.stepType)) {
          processChildStep(childStep)
        }
      }

      if (step.childStepFailureResultCount == 0)
        step.result = new Result(Result.SUCCEEDED)
      else {
        step.result = new Result(Result.FAILED)
      }

      listener.gotResult step.result

      if (isRealScenario && currentContext.afterEach)
        processScenario(currentContext.afterEach, false)

    } finally {
      listener.stopStep()

      if (isRealScenario)
        currentContext.notifyPlugins { plugin, binding -> plugin.afterScenario(binding) }
    }
  }
}