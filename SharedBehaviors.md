# Introduction #

As of easyb 0.9.6, the notion of _shared behaviors_ is supported; that is, you can create a base behavior (at this point it must live within the context of a single story (i.e. a file)) and then refer to that behavior inline using the keywords `shared_behavior` and `it_behaves_as` like so:

```
shared_behavior "shared behaviors", {
  given "a string", {
    var = ""
  }
  
  when "the string is hello world", {
    var = "hello world"
  }
}

scenario "first scenario", {
  it_behaves_as "shared behaviors"
  
  then "the string should start with hello", {
    var.shouldStartWith "hello"
  }
}

scenario "second scenario", {
  it_behaves_as "shared behaviors"
  
  then "the string should end with world", {
    var.shouldEndWith "world"
  }
}
```
