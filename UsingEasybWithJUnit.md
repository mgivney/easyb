The [easyb-junit](http://code.google.com/p/easyb-junit/) project facilitates running easyb behaviors via JUnit. By extending the project's `EasybSuite` class, you can then have any JUnit runner (Eclipse, Ant, etc) execute behaviors found in a specified directory. The project's homepage documents this nicely.

What's more, because instances of `EasybSuite` are JUnit instances, valid JUnit reports can be created. Thus, you can use an instance of `EasybSuite` in a project that leverages Hudson (for instance) to track behavior status and history.

For example, in an Ant project monitored by Hudson, you can run your behaviors like so:

```
<target name="junit" depends="compile" description="runs easyb stories via junit">
 <mkdir dir="./target/test-reports" />
 <junit printsummary="yes" haltonfailure="yes">
  <classpath refid="classpath" />
  <formatter type="xml" />
  <batchtest fork="yes" todir="./target/test-reports">
   <fileset dir="test/java">
    <include name="**/*EasybBehaviorRunner*.java" />
   </fileset>
  </batchtest>
 </junit>
</target>
```

Then in Hudson, for the build step, execute the task "junit" and you'll have a test graph and historical data available for subsequent builds.