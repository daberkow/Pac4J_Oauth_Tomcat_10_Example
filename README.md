# Pac4J_Oauth_Tomcat_10_Example

An example of using embedded Tomcat 10 With Pac4J and Generic OAuth. I spent a chunk of time learning how to integrate Tomcat 10 (Jakarta) with Pac4J and Keycloak. This code is an example to anyone who needs to do similar. I have also thrown in some examples of Tomcat security settings which can [help you get through a Nessus scan](https://buildingtents.com/2022/10/26/clean-tenable-nessus-scans-for-rhel-7-with-podman/).

Targeting: Java 17, PAC4J 5.7, Tomcat 10.1

## Using this Repo

1. Follow the guide on [my blog post to get Keycloak setup](https://buildingtents.com/2022/12/29/pac4j-integration-with-embedded-tomcat-10-using-generic-oauth-via-keycloak/), or be prepared with your own OAuth server
2. Clone this repo down
3. Edit `.\src\main\java\com\github\daberkow\pac4j_oauth_tomcat_10_example\oauth\AbstractAuth.class` with your client ID (Key) and secret
4. On Windows `.\gradlew.bat build run`, on Linux/Mac `./gradlew build run`
5. Browse to `http://127.0.0.1:8080`

Please keep in mind this is demonstration code, not production ready code.

Some added things to play with, I included 2 plugins for Gradle I always like to use.

First [Shadow](https://github.com/johnrengelman/shadow) this creates a single jar with all your dependencies to easily package your app up. Running `.\gradle.bat shadowJar` will drop a file named `Pac4J_Oauth_Tomcat_10_Example-all.jar` in `.\build\libs\`. Then running `java -jar Pac4J_Oauth_Tomcat_10_Example-all.jar` will run the whole app.

Second [The Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) this adds the `dependencyUpdates` task to Gradle, making it one command to see which of your dependencies need upgraded.

Hope this repo helps someone out there, if it does please star it or drop a comment over at the blog!