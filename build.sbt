import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.SbtArtifactory
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings
import play.sbt.routes.RoutesKeys
import scoverage.ScoverageKeys
import uk.gov.hmrc.SbtAutoBuildPlugin

val appName = "coronavirus-jrs-calculator-frontend"

lazy val scoverageSettings = {
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := """uk\.gov\.hmrc\.BuildInfo;.*\.Routes;.*\.RoutesPrefix;.*Filters?;MicroserviceAuditConnector;Module;GraphiteStartUp;.*\.Reverse[^.]*""",
    ScoverageKeys.coverageMinimum := 80.00,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    parallelExecution in Test := false
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory)
  .settings(
    majorVersion := 0,
    name := appName,
    scalaVersion := "2.11.12",
    PlayKeys.playDefaultPort := 9264,
    RoutesKeys.routesImport := Seq.empty,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    resolvers := Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.bintrayRepo("hmrc", "release-candidates"),
      Resolver.typesafeRepo("releases"),
      Resolver.jcenterRepo
    ),
    publishingSettings,
    scoverageSettings,
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources",
    scalafmtOnCompile in Compile := true,
    scalafmtOnCompile in Test := true,
    routesGenerator := InjectedRoutesGenerator
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings(): _*)
  .settings(
      Keys.fork in IntegrationTest := true,
      Defaults.itSettings,
      unmanagedSourceDirectories in IntegrationTest += baseDirectory(_ / "it").value,
      parallelExecution in IntegrationTest := false,
      scalafmtOnCompile in IntegrationTest := true
  )
  .settings(addCompilerPlugin(scalafixSemanticdb))
  .settings(
      scalacOptions ++= List(
          "-Yrangepos",
          "-Xplugin-require:semanticdb",
          "-P:semanticdb:synthetics:on",
          "-Xfatal-warnings",
          "-Xlint:-missing-interpolator,_",
          "-Yno-adapted-args",
          "-Ywarn-value-discard",
          "-Ywarn-dead-code",
          "-deprecation",
          "-feature",
          "-unchecked",
          "-language:implicitConversions"
      )
  )