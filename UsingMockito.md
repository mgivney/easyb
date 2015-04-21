#Mockito is a slick mocking framework that works just fine w/easyb

The question of whether or not one can use mocking frameworks in easyb arises from time to time. The answer is a resounding "YES!" -- pretty much anything you can do in normal Java/JUnit, you can do in easyb! Watch!

Using [Mockito's own examples](http://mockito.googlecode.com/svn/branches/1.8.1-rc1/javadoc/org/mockito/Mockito.html), you can use both stories and specifications to mock objects. Here are a few easyb + mockito examples:

```
import static org.mockito.Mockito.*

scenario "stubbing in mockito works", {
  given "a mocked object", {
    mockedList = mock(LinkedList.class)
  }
  when "it is stubbed", {
    when(mockedList.get(0)).thenReturn("first")
    when(mockedList.get(1)).thenThrow(new RuntimeException())
  }
  then "verifications should work", {
    mockedList.get(0).shouldBe "first"
    ensureThrows(RuntimeException) {
      mockedList.get(1)
    }
    mockedList.get(9).shouldBe null
  }
}
```

You can use the same example in specs too:

```

import static org.mockito.Mockito.*

mockedList = mock(LinkedList.class)
 
it "should support stubbing method calls", {
 when(mockedList.get(0)).thenReturn("first")
 when(mockedList.get(1)).thenThrow(new RuntimeException())
}
 
it "should behave like a mocked list" , { 
 mockedList.get(0).shouldBe "first"
 ensureThrows(RuntimeException){
   mockedList.get(1)
 }
 mockedList.get(9).shouldBe null	
}
```

You can also do normal verifications, etc:

```

description "mockito should work w/easyb"

import static org.mockito.Mockito.*

mockedList = mock(List.class)

it "should support normal usage of methods", {
 mockedList.add("one")
 mockedList.clear()	
}

it "should also support normal verification", {
 verify(mockedList).add("one")
 verify(mockedList).clear()	
}
```