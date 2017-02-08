// Comment to get more information during initialization
// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

//resolvers += Resolver.sonatypeRepo("releases")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.0")

//plugins for javaEbean
addSbtPlugin("com.typesafe.sbt" % "sbt-play-ebean" % "3.0.0")

// c.f https://github.com/sbt/sbt-web for more awesome plugins

// c.f https://github.com/sbt/sbt-digest
// hashed file are created only in prod mode
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")


addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

// c.f https://github.com/rgcottrell/sbt-imagemin
addSbtPlugin("com.slidingautonomy.sbt" % "sbt-imagemin" % "1.0.1")


// c.f https://github.com/rgcottrell/sbt-html-minifier
//addSbtPlugin("com.slidingautonomy.sbt" % "sbt-html-minifier" % "1.0.0")

// TODO figure out why compression breaks css in home page
// c.f https://github.com/ground5hark/sbt-css-compress#sbt-css-compress
//addSbtPlugin("net.ground5hark.sbt" % "sbt-css-compress" % "0.1.4")
//
// c.f https://github.com/sbt/sbt-uglify
//addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")
