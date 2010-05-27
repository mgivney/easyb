package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.delegates.EnsuringDelegate
import org.easyb.result.Result
import org.easyb.util.BehaviorStepType

class SpecificationKeywords extends BehaviorKeywords {
  private BehaviorStepStack stepStack
  private def beforeIt
  private def afterIt

  SpecificationKeywords(ExecutionListener listener) {
    super(listener)

    stepStack = new BehaviorStepStack(listener)
  }


  def after(description, closure) {
    stepStack.startStep(BehaviorStepType.AFTER, description)
    afterIt = closure
    stepStack.stopStep()
  }

  def before(description, closure) {
    stepStack.startStep(BehaviorStepType.BEFORE, description)
    beforeIt = closure
    stepStack.stopStep()
  }

  def it(spec, closure) {
    stepStack.startStep(BehaviorStepType.IT, spec)
    closure.delegate = new EnsuringDelegate()
    try {
      if (beforeIt != null) {
        beforeIt()
      }
      listener.gotResult(new Result(Result.SUCCEEDED))
      use(BehaviorCategory) {
        closure()
      }
      if (afterIt != null) {
        afterIt()
      }
    } catch (Throwable ex) {
      listener.gotResult(new Result(ex))
    } finally {
      stepStack.stopStep()
    }
  }

  def and() {
    stepStack.startStep(BehaviorStepType.AND, "")
    stepStack.stopStep()
  }
}