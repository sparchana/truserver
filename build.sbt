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
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "junit" % "junit" % "4.12",
  "com.novocode" % "junit-interface" % "0.11" % "test"
  )


libraryDependencies += "com.google.api-client" % "google-api-client" % "1.20.0"

libraryDependencies += "com.google.protobuf" % "protobuf-java" % "3.0.0-beta-2"

libraryDependencies += "net.sf.opencsv" % "opencsv" % "2.3"

libraryDependencies += evolutions
