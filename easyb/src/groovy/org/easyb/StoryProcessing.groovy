package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.result.Result

import org.easyb.util.BehaviorStepType

public class StoryProcessing {
  Stack<StoryContext> contextStack = new Stack<StoryContext>();
  StoryContext currentContext = null
  boolean executeStory
  ExecutionListener listener
  int currentIteration

  public def processStory(StoryContext currentContext, boolean executeStory, ExecutionListener listener) {
    this.executeStory = executeStory
    this.listener = listener

    runContext(currentContext)
  }

  private def processStoryContext(StoryContext context) {
    context.steps.each { BehaviorStep step ->
      if (step.stepType == BehaviorStepType.SCENARIO) {
        if (step.ignore) {
          listener.startStep step
          listener.gotResult new Result(Result.IGNORED)
          listener.stopStep()
        } else if (step.pending) {
          listener.startStep step
          step.closure()
          listener.stopStep()
        } else if (step.exampleData) {
          processScenarioWithExamples step
        } else {
          processScenario step, true
        }
      } else if (step.stepType == BehaviorStepType.EXAMPLES) {
        runContext(step.storyContext)
      } else {
        processChildStep step
      }
    }
  }

  private def extractMapExampleData(map) {
    int max = 0
    def fields = []

    map.each { key, value ->
      fields.add(key)

      if (!max)
        max = value.size()
    }

    return [max, fields]
  }


  private void processScenariosWithExampleMap(BehaviorStep scenario) {
    def (int max, fields) = extractMapExampleData(scenario.exampleData)

    int oldIteration = currentIteration
    currentIteration = 0

    try {
      for (int idx = 0; idx < max; idx++) {
        fields.each { field ->
          StoryContext.binding.setProperty field, scenario.exampleData[field][idx]
        }

        processScenario(scenario, true)
      }
    } finally {
      currentIteration = oldIteration
    }
  }

  private void processScenarioWithExamples(BehaviorStep scenario) {
    if (scenario.exampleData instanceof Map) {
      processScenariosWithExampleMap scenario
    } else {
      throw new IncorrectGrammarException("Don't know how to process example data in scenario ${scenario.name}")
    }
  }

  private def processStepsUsingMap(StoryContext context, map) {
    def (int max, fields) = extractMapExampleData(map)
    int oldIteration = currentIteration
    currentIteration = 0

    try {
      for (int idx = 0; idx < max; idx++) {
        fields.each { field ->
          context.binding.setProperty field, map[field][idx]
        }

        processStoryContext(context)
      }
    } finally {
      currentIteration = oldIteration
    }
  }

  private def processClosure(exampleData) {
    def expando = new Expando( ['story':StoryContext.binding] )
    
    Closure c = exampleData
    c.resolveStrategy = Closure.DELEGATE_FIRST
    c.delegate = expando

    c.call()

    def map = expando.getProperties()
    map.remove('story')

    // if there is only 1 item and its a map, then use that instead
    if ( map.size() == 1 ) {
      def item = map.values().asList()
      if ( item[0] instanceof Map )
        map = item[0]
    }

    println "map is ${map}"

    return map
  }

  /*
    runs all of the scenarios, befores and afters in this context
   */

  private def runContext(StoryContext context) {
    if (currentContext != null)
      contextStack.push(currentContext)

    try {
      this.currentContext = context

      // let the plugins know we are starting
      context.notifyPlugins { plugin, binding ->
        plugin.setClassLoader(getClass().getClassLoader())
        plugin.beforeStory(binding)
      }

      if (context.beforeScenarios)
        processScenario(context.beforeScenarios, false)

      try {
        if (!context.exampleData)
          processStoryContext(context)
        else if (context.exampleData instanceof Map) {
          if (context.exampleData.size()) {
            processStepsUsingMap(context, context.exampleData)
          }
        } else if ( context.exampleData instanceof Closure ) {
          def map = processClosure(context.exampleData)
          processStepsUsingMap(context, map)
        }
      } finally {
        if (context.afterScenarios)
          processScenario(context.afterScenarios, false)

        context.notifyPlugins { plugin, binding -> plugin.afterStory(binding) }
      }
    } finally {
      if (contextStack.size())
        currentContext = contextStack.pop()
    }

  }


  private def processSharedScenarios(sharedStep) {
    BehaviorStep shared = currentContext.sharedScenarios[sharedStep.name]

    if (!shared) { // can't find the shared scenario
      listener.startStep(sharedStep)

      sharedStep.result = new Result(Result.FAILED)
      sharedStep.result.description = "Unable to find shared scenario ${sharedStep.name}"

      listener.gotResult sharedStep.result

      listener.stopStep()
    } else {
      shared.cloneStep(sharedStep)  // copy shared behaviour into this object
      processScenario sharedStep, false
    }

    println "out of shared, back to original"
  }

  private def processChildStep(BehaviorStep childStep) {
    childStep.decodeCurrentName StoryContext.binding, currentIteration
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

    step.decodeCurrentName StoryContext.binding, currentIteration
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
          processSharedScenarios(childStep)
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