package org.disco.easyb.core.listener

import org.disco.easyb.core.result.Result

scenario "listener is given all successful results", {

  given "a story listener",{
    listener = new DefaultListener()
  }

  when "a successful result is added", {
    result = new Result("irrelevant", "irrelevant", Result.SUCCEEDED)
    listener.gotResult(result)
  }

  then "it should have no failures", {
    ensure(listener.hasBehaviorFailures()) {
      isFalse
    }
  }

  and

  then "the count of failed specifications should be 0", {
    ensure(listener.getFailedBehaviorCount()) {
      isEqualTo0
    }
  }

  and

  then "the count of successful specifications should be 1", {
    ensure(listener.getSuccessfulBehaviorCount()) {
      isEqualTo1
    }
  }

  and

  then "the count of total specifications should be 1", {
    ensure(listener.getTotalBehaviorCount()) {
      isEqualTo1
    }
  }

  and

  then "the total specifications should equal the successful specifications", {
    ensure(listener.getTotalBehaviorCount()) {
      isEqualTo listener.getSuccessfulBehaviorCount()
    }
  }

}

scenario "listener is given a single failure", {

  given "a story listener", {
    listener = new DefaultListener()
  }

  when "a failure result is added", {
    result = new Result("irrelevant", "irrelevant", new Exception("FailureExceptionReason"))
    listener.gotResult(result)
  }

  then "it should have failures", {
    ensure(listener.hasBehaviorFailures()) {
      isTrue
    }
  }

  and

  then "the count of failed specifications should be 1", {
    ensure(listener.getFailedBehaviorCount()) {
      isEqualTo1
    }
  }

  and

  then "the count of successful specifications should be 0", {
    ensure(listener.getSuccessfulBehaviorCount()) {
      isEqualTo0
    }
  }

  and

  then "the count of total specifications should be 1", {
    ensure(listener.getTotalBehaviorCount()) {
      isEqualTo1
    }
  }

  and

  then "the total specifications should equal the failed specifications", {
    ensure(listener.getTotalBehaviorCount()) {
      isEqualTo listener.getFailedBehaviorCount()
    }
  }

}
