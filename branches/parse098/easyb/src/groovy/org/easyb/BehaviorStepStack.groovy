package org.easyb

import org.easyb.listener.ExecutionListener

class BehaviorStepStack {
  ExecutionListener listener

  public BehaviorStepStack(ExecutionListener listener) {
    this.listener = listener
  }

  def startStep( behaviorType, scenarioDescription) {
    BehaviorStep step = new BehaviorStep (behaviorType, scenarioDescription, null)
    listener.startStep(step)
  }

  def lastStep() {
    throw new RuntimeException("don't create me")
  }

  def stopStep() {
    this.listener.stopStep()
  }


  def replaySteps() {
    throw new RuntimeException("don't create me")
  }

  String toString() {
    "step stack has ${steps.size ()} items in it."
  }
}