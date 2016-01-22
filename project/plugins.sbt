resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
        Resolver.ivyStylePatterns)

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.9")
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.3.5")
addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.0.3")
