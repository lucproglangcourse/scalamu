organization := "edu.luc.etl"

name := "scalamu"

version := "0.2.2"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

seq(bintrayPublishSettings:_*)

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:higherKinds")

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.5",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "org.scalacheck" %% "scalacheck" % "1.10.1" % "test",
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.0.5" % "test"
)

autoAPIMappings := true

apiURL := Some(url("http://loyolachicagocode.github.io/scalamu/doc"))

initialCommands in console := """
                                |import scalaz._
                                |import Scalaz._
                                |import scalamu._
                                |""".stripMargin
