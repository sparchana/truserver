name := "trujobs"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.7"

routesGenerator := StaticRoutesGenerator

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.27",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test"
  )

libraryDependencies += evolutions
