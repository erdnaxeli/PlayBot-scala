val scala3Version = "3.1.2"
val commonSettings = Seq(
  assembly / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.discard
    case x =>
      val oldStrategy = (assembly / assemblyMergeStrategy).value
      oldStrategy(x)
  },
  scalaVersion := scala3Version,
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-explain-types", // Explain type errors in more detail.
    // "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen", // Warn when numerics are widened.
    "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals", // Warn if a local definition is unused.
    "-Ywarn-unused:params", // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates", // Warn if a private member is unused.
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
  )
)

lazy val core = project
  .settings(
    commonSettings,
    name := "PlayBot-core",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "com.google.api-client" % "google-api-client" % "1.23.0",
      "com.google.apis" % "google-api-services-youtube" % "v3-rev222-1.25.0",
      "com.softwaremill.sttp.client3" %% "core" % "3.6.2",
      //"com.softwaremill.sttp.client3" %% "play-json" % "3.6.2", // no scala3 support yet
      "com.typesafe" % "config" % "1.4.1",
      "com.typesafe.play" %% "play-json" % "2.10.0-RC6",
      "org.mariadb.jdbc" % "mariadb-java-client" % "2.7.4",
      "org.scalatest" %% "scalatest" % "3.2.10" % "test",
      "pircbot" % "pircbot" % "1.5.0"
    ),
    run / fork := true
  )

lazy val irc = project
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "PlayBot-irc",
    version := "0.1.0-SNAPSHOT"
  )

lazy val http = project
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "PlayBot-http",
    version := "0.1.0-SNAPSHOT",
    run / fork := true,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "1.4.0",
      "pircbot" % "pircbot" % "1.5.0"
    )
  )

lazy val cli = project
  .dependsOn(core)
  .settings(
    commonSettings,
    name := "PlayBot-cli",
    version := "0.1.0-SNAPSHOT"
  )
