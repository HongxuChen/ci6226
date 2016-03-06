name := """simple-rest-scala"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

lazy val root = project.in(file(".")).enablePlugins(PlayScala)


//libraryDependencies += "com.google.guava" % "guava" % "18.0"

libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "5.4.1"
libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "5.4.1"
//libraryDependencies += "ch.qos.logback" % "logback-core" % "1.1.5"
//libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.5"
libraryDependencies += "net.sourceforge.argparse4j" % "argparse4j" % "0.7.0"