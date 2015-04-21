Since Selenium was introduced a few years back, it has continued to wow developers with how easily a user acceptance test can be knocked out– simply fire up an instance of a Selenium server in the background and then either write a table test or a RC style test– it’s that easy.

RC style testing is particularly powerful as you have full access to programming languages– for instance, with RC, you can write a functional web test in Java by leveraging a framework like JUnit or TestNG. But what’s often lacking with testing frameworks is a more natural way of expressing behavior– or indeed, scenarios and stories.

For instance, a user acceptance test is really a scenario– a user logs into a website, purchases an item, pays, and logs out. That was a sunny day scenario– there are other scenarios that deal with various other paths– user fails to pay, credit card was invalid, etc. All of these scenarios are logically a story– a story about buying something.

Using a standard scenario language, one can more specifically write a scenario (in a story regarding a website for race registrations) like so:
  * given a user is on the race report page
  * when someone enters a first name and last name in the race report form for someone who has signed up for a race
  * then they should receive a list of all races that person has singed up for

That is a happy day scenario isn’t it? One particular negative path would be:
  * given a user is on the race report page
  * when someone enters a first name and last name in the race report form who hasn’t signed up for any races
  * then they should receive a message indicating the person hasn’t signed up for any races

Using easyb then, one can create a story file, which contains two scenarios– the file could be called `RaceReport.story` and will have two scenarios:

```
scenario "a valid person has been entered", {}
scenario "an invalid person has been entered", {}
```

Given that the plan is to leverage Selenium, two fixtures can be used like so:
```
before "start selenium", {
 given "selenium is up and running", {
  selenium = new DefaultSelenium("localhost",
   4444, "*firefox", "http://acme.racing.net/greport")
  selenium.start()
 }
}
```

Note how this fixture connects to a server instance running on the same machine, which will utilize Firefox.

Next, two chained clauses can simulate a user interacting with the report page.

```
when "filling out the person form with a first and last name", {
 selenium.open("http://acme.racing.net/greport/personracereport.html")
 selenium.type("fname", "Britney")
 selenium.type("lname", "Smith")
}

and "the submit link has been clicked", {
 selenium.click("submit")
}
```

The `then` clause then verifies that 4 race instances have been returned for a user– note how using Groovy's `for` loop which uses a positional index to grab items from a list and from an XPath expression is fairly easy!

```
then "the report should have a list of races for that person", {
 selenium.waitForPageToLoad("5000")
 values = ["Mclean 1/2 Marathon", "Reston 5K", "Herndon 10K", "Leesburg 10K"]
 for(i in 0..<values.size()){
  selenium.getText("//table//tr[${(i+3)}]/td").shouldBeEqualTo values[i]
 }
}
```

Lastly, selenium needs to be shut down:

```
after "stop selenium" , {
 then "selenium should be shutdown", {
  selenium.stop()
 }
}
```

The entire first scenario looks like this once you put it all together:

```

before "start selenium", {
 given "selenium is up and running", {
  selenium = new DefaultSelenium("localhost",
   4444, "*firefox", "http://acme.racing.net/greport")
  selenium.start()
 }
}

scenario "a valid person has been entered", {

 when "filling out the person form with a first and last name", {
  selenium.open("http://acme.racing.net/greport/personracereport.html")
  selenium.type("fname", "Britney")
  selenium.type("lname", "Smith")
 }
 
 and "the submit link has been clicked", {
  selenium.click("submit")
 }

 then "the report should have a list of races for that person", {
  selenium.waitForPageToLoad("5000")
  values = ["Mclean 1/2 Marathon", "Reston 5K", "Herndon 10K", "Leesburg 10K"]
  for(i in 0..<values.size()){
   selenium.getText("//table//tr[${(i+3)}]/td").shouldBeEqualTo values[i]
  }
 }

}

after "stop selenium" , {
 then "selenium should be shutdown", {
  selenium.stop()
 }
}

```

That’s pretty easy, don’t you think? Of course, the next step is to implement some additional scenarios, such as negative paths with an non-existing runner, etc.


Functional web stories are a powerful mechanism to verify the proper behavior of web applications from a user’s standpoint. Combining a framework that supports stories and scenarios with Selenium yields an easy way to deliver software more quickly and collaboratively.