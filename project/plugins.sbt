// Comment to get more information during initialization
// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.0")

//plugins for javaEbean
addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "3.0.0")

// c.f https://github.com/sbt/sbt-digest
// hashed file are created only in prod mode
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")


addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")