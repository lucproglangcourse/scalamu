organization := "edu.luc.etl"

name := "scalamu"

version := "0.2.4"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.6", "2.11.7")

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

seq(bintrayPublishSettings:_*)

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-language:higherKinds")

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.2.0",
  "org.scalatest" %% "scalatest" % "2.2.6" % Test,
  "org.scalacheck" %% "scalacheck" % "1.12.5" % Test,
  "org.scalaz" %% "scalaz-scalacheck-binding" % "7.2.0" % Test
)

autoAPIMappings := true

apiURL := Some(url("http://loyolachicagocode.github.io/scalamu/doc"))

initialCommands in console := """
                                |import scalaz._
                                |import Scalaz._
                                |import scalamu._
                                |""".stripMargin
