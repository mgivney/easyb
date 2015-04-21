Code coverage, regardless of your desired tool, essentially entails three steps, namely:
  * instrument your code base
  * run your tests (alternately, run something as tests are necessarily a requirement)
  * produce a report

Instrumenting your code base with Cobertura is straightforward — Cobertura’s documentation is quite good and you should be able to come close to having a working version with a simple copy and paste. For example, here is an instrument task in Ant:
```
<target name="instrument" depends="compile-source" description="instruments .class files">
 <property name="cobertura.dir" value="lib/cobertura" />
   <path id="cobertura.classpath">
    <fileset dir="${cobertura.dir}">
      <include name="**/*.jar" />
    </fileset>
   </path>
 <taskdef classpathref="cobertura.classpath" resource="tasks.properties" />
 <delete file="./cobertura.ser" />
 <mkdir dir="target/target-inst" />

 <cobertura-instrument datafile="cobertura.ser" todir=".">
   <fileset dir="target/classes">
     <include name="**/*.class" />
	 <exclude name="**/*Test.class" />
    </fileset>
 </cobertura-instrument>
</target>
```

Next, execute your easyb behaviors as normal; however, be sure to that your classpath references your instrumented .class files — otherwise, you’ll have reports telling you of 0% coverage!

```
<target name="coverage" depends="instrument" description="runs code coverage">
 <mkdir dir="target/reports" />
  <easyb failureProperty="easyb.failed">
	<classpath>
      <path refid="build.classpath"/>
      <pathelement path="target/target-inst"/>
    </classpath>

   <report location="target/stories.txt" format="txtstory"/>
   <behaviors dir="stories">
    <include name="**/**.story"/>
   </behaviors>
  </easyb>
  <fail if="easyb.failed" message="easyb reported a failure"/>

  <mkdir dir="target/reports/coverage-report" />
  <cobertura-report format="html" datafile="cobertura.ser"
	destdir="target/coverage-report" srcdir="src/" />
  <cobertura-report format="xml" datafile="cobertura.ser"
	destdir="target/coverage-report" srcdir="src/" />
</target>
```

You’ll note that this task does a few things — that is, it runs my easyb stories and produces a coverage report. And that’s about it! With these two tasks defined, when you execute `%>ant coverage`, you’ll have code coverage reports with Cobertura that measure the coverage of your easyb behaviors. Keep in mind, you must force easyb to load your instrumented .class files — that is, your stories are not compiled but the code those stories reference (whether they be Java or Groovy) must be instrumented for a report to be generated (as those are .class files that are loaded by the Groovy runtime).

Incidentally, by applying strict BDD semantics — that is, defining stories before writing any code and then writing just enough code to have one scenario at a time pass produces 100% code coverage in every occasion that I’ve employed. Incidentally, even though one has 100% code coverage it doesn’t mean the code is defect free.