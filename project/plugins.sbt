resolvers += Resolver.url(
  "bintray-sbt-plugin-releases",
    url("http://dl.bintray.com/content/sbt/sbt-plugin-releases"))(
        Resolver.ivyStylePatterns)

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.1.2")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.3.5")

addSbtPlugin("org.scoverage" %% "sbt-coveralls" % "1.0.0.BETA1")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.9")
