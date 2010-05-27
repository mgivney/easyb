package org.easyb

import org.easyb.listener.ExecutionListener
import org.easyb.delegates.NarrativeDelegate
import org.easyb.listener.ResultsCollector
import org.easyb.listener.BroadcastListener
import org.easyb.util.BehaviorStepType

class BehaviorKeywords {
    ExecutionListener listener
    BehaviorStepStack stepStack

    BehaviorKeywords(ExecutionListener listener) {
        this.listener = listener

      stepStack = new BehaviorStepStack(listener)
    }

    def narrative(description, closure) {
        stepStack.startStep BehaviorStepType.NARRATIVE, description
        listener.describeStep description
        closure.delegate = new NarrativeDelegate(listener, stepStack)
        closure()
        stepStack.stopStep()
    }

    def description(description) {
        stepStack.startStep BehaviorStepType.DESCRIPTION, description
        listener.describeStep(description)
        stepStack.stopStep()
    }
  
    ResultsCollector easybResults() {
        if (listener instanceof ResultsCollector) {
            return (ResultsCollector) listener
        }

        if (listener instanceof BroadcastListener) {
            BroadcastListener broadcaster = (BroadcastListener) listener
            def result = broadcaster.listeners.find {
                if (it instanceof ResultsCollector)
                    return true
            }
            if (result != null)
                return (ResultsCollector) result
        }

        throw new IllegalStateException('No results collector available to provide easyb results')
    }
}