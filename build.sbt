import play.sbt.routes.RoutesKeys
import sbt.Def
import scoverage.ScoverageKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val appName: String = "coronavirus-jrs-calculator-frontend"

val silencerVersion = "1.6.0"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtAutoBuildPlugin, SbtDistributablesPlugin, SbtArtifactory)
  .settings(DefaultBuildSettings.scalaSettings: _*)
  .settings(DefaultBuildSettings.defaultSettings(): _*)
  .settings(SbtDistributablesPlugin.publishingSettings: _*)
  .settings(inConfig(Test)(testSettings): _*)
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := true,
    unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest) (base => Seq(base / "it")).value,
    parallelExecution in IntegrationTest := false,
    javaOptions += "-Dlogger.resource=logback-test.xml"
  )
  .settings(majorVersion := 0)
  .settings(useSuperShell in ThisBuild := false)
  .settings(
    // silence all warnings on autogenerated files
    scalacOptions += "-P:silencer:pathFilters=target/.*",
    // Make sure you only exclude warnings for the project directories, i.e. make builds reproducible
    scalacOptions += s"-P:silencer:sourceRoots=${baseDirectory.value.getCanonicalPath}",
    // Suppress warnings due to mongo dates using `$date` in their Json representation
    scalacOptions += "-P:silencer:globalFilters=possible missing interpolator: detected interpolated identifier `\\$date`",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
  )
  .settings(
    scalaVersion := "2.12.10",
    name := appName,
    RoutesKeys.routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.govukfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "models.Mode",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 9264,
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*repositories.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController",
    ScoverageKeys.coverageMinimum := 70,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true,
    scalacOptions ++= Seq("-feature"),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged := true,
    scalafmtOnCompile in Compile := true,
    scalafmtOnCompile in Test := true,
    evictionWarningOptions in update :=
      EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    resolvers ++= Seq(
      Resolver.bintrayRepo("hmrc", "releases"),
      Resolver.jcenterRepo
    ),
    // concatenate js
    Concat.groups := Seq(
      "javascripts/application.js" ->
        group(Seq(
          "lib/govuk-frontend/govuk/all.js",
          "javascripts/jquery.min.js",
          "javascripts/details.polyfill.js",
          "javascripts/timeoutDialog.js",
          "javascripts/app.js"
        ))
    ),
    // prevent removal of unused code which generates warning errors due to use of third-party libs
    uglifyCompressOptions := Seq("unused=false", "dead_code=false"),
    pipelineStages := Seq(digest),
    // below line required to force asset pipeline to operate in dev rather than only prod
    pipelineStages in Assets := Seq(concat,uglify),
    // only compress files generated by concat
    includeFilter in uglify := GlobFilter("application.js")
  )
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427

lazy val testSettings: Seq[Def.Setting[_]] = Seq(
  fork        := true,
  javaOptions ++= Seq(
    "-Dconfig.resource=test.application.conf",
    "-Dlogger.resource=logback-test.xml"
  )
)

dependencyOverrides ++= AppDependencies.overrides
