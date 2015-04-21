# Introduction #

A brief outline of how to release the easyb artifacts for use by Maven builds. This works the same on all easyb artifacts.


# Details #

For more details on the general Maven release process using the Sonatype OSS Maven repository, see the documentation at [the Sonatype OSS Maven Repo Usage Guide](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide)

  * sign up for an account to be able to deploy easyb on the Sonatype OSS Nexus instance

  * in your Maven settings.xml file, make sure you have a repository called "sonatype-nexus-staging" with your Nexus login and password

  * maven sure you have a
```
         <profile>
            <id>sonatype</id>
            <properties>
              <gpg.passphrase>whatever</gpg.passphrase>
            </properties>
          </profile>
```
> > profile in your settings.xml file

  * use the standard mvn release:prepare && mvn release:perform tasks to release the artifact to the Nexus repository.

  * once the artifacts are successfully deployed to the Nexus repo, log in to Nexus to see the newly created staging repository (should be something like "org.easyb-XXXX (u: username a: IP-address)" )

  * to finalize the release, "close" the staging repository by selecting the staging repository in the Nexus UI and clicking the "close" button at the top of the window.

  * if everything has gone smoothly, the repository should now be "closed".  If there were any error messages, chances are there is something wrong with the pom file for the project.  Fix the problems, drop the repository, and re-upload the artifacts.

  * to release the artifacts in the staging repository, select the closed staging repository, and click the "release" button at the top of the window

  * once the artifacts are released successfully, they should be automatically synced with Maven central.  Check at [Maven Central](http://repo1.maven.org/maven2/org/easyb/easyb/)  after a few hours to be sure.