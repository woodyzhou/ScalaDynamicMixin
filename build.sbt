name := "ScalaDynamicMixin"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "junit" % "junit" % "4.11" % Test,
  "org.scalatest" %% "scalatest" % "2.2.1" % Test,
  "org.specs2" %% "specs2" % "2.4.9" % Test
)
