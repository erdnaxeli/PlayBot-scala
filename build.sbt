val scala3Version = "3.1.0"

lazy val doobieVersion = "1.0.0-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "PlayBot",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.10" % "test",
      "com.google.api-client" % "google-api-client" % "1.23.0",
      "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0",
      "com.typesafe" % "config" % "1.4.1",
      "pircbot" % "pircbot" % "1.5.0",
      "org.mariadb.jdbc" % "mariadb-java-client" % "2.7.4"
    ),
    run / fork := true
  )
