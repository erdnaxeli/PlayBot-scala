val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "PlayBot",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test",
    libraryDependencies += "com.google.api-client" % "google-api-client" % "1.23.0",
    libraryDependencies += "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0",
    libraryDependencies += "com.typesafe" % "config" % "1.4.1",
    libraryDependencies += "pircbot" % "pircbot" % "1.5.0",
    run / fork := true,
  )
