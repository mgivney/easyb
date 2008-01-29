package org.disco.bdd.story.stack

import org.disco.bdd.Stack

scenario "pop is called on stack with one value", {

  given "an empty stack with one pushed value",{
    stack = new Stack()
    pushVal = "foo"
    stack.push(pushVal)
  }

  when "pop is called", {
    popVal = stack.pop()
  }

  then "that object should be returned", {
    ensure(popVal) {
      isEqualTo pushVal
    }
  }

  and

  then "the stack should be empty", {
      ensure(stack.empty) {
        isTrue
      }
    }
}


scenario "stack with one value is not empty", {

  given "an empty stack with one pushed value",{
    stack = new Stack()
    stack.push("bar")
  }

  then "the stack should not be empty", {
    ensure(stack.empty) {
      isFalse
    }
  }
}

scenario "peek is called", {

  given "a stack containing an item",{
    stack = new Stack()
    push1 = "foo"
    stack.push(push1)
  }

  when "peek is called", {
    peeked = stack.peek()
  }

  then "it should provide the value of the most recent pushed value", {
    ensure(peeked) {
      isEqualTofoo
    }
  }

  and

  then "the stack should not be empty", {
      ensure(!stack.empty)
    }

  and

  then "calling pop should also return the peeked value which is the same as the original pushed value", {
      ensure(stack.pop()) {
        isEqualTo(push1)
        and
        isEqualTo(peeked)
      }
    }

  and

  then "the stack should  be empty", {
      ensure(stack.empty) {
        isTrue
      }
    }
}
