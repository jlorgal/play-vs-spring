name := "play"
 
version := "1.0"

lazy val `play` = (project in file(".")).enablePlugins(PlayJava)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.12.6"

libraryDependencies += guice
libraryDependencies += "org.projectlombok" % "lombok" % "1.18.4"
libraryDependencies += "io.dropwizard.metrics" % "metrics-core" % "3.2.1"
libraryDependencies += "org.codehaus.plexus" % "plexus-utils" % "3.0.18"
libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.1"
libraryDependencies += "com.google.guava" % "guava" % "22.0"
libraryDependencies += "com.palominolabs.http" % "url-builder" % "1.1.0"
libraryDependencies += "net.jodah" % "failsafe" % "1.0.3"
libraryDependencies += "org.mongodb" % "mongodb-driver-reactivestreams" % "1.10.0"

PlayKeys.externalizeResources := false
