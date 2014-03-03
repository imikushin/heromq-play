import play.Project._

organization := "com.killingbilling"

name := "heromq-play"

version := "2.0-SNAPSHOT"

playScalaSettings

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-slf4j" % "1.0.1",
  "org.scala-lang" % "scalap" % "2.10.3"
)
