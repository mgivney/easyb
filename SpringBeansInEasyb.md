# Introduction #

There is nothing stopping you from using your spring beans in easyb, in fact its quite easy.


With a spring definition of:
```
<bean id="widgetDao" class="examples.WidgetDaoImpl"/>
```

A behavior can simply get an instance of the WidgetDaoImpl thru the application context:
```
given "the widget instance has been obtained from Spring", {
 context =
  new ClassPathXmlApplicationContext("spring-config.xml")
 widgetDao = context.getBean("widgetDao")
}

when "findWidget is called with a valid id", {
 widget = widgetDao.findWidget("1")
}

then "a working Widget object should be returned", {
 widget.shouldNotBe null
  and
 widget.lineItems.size().shouldEqual 2
}
```