import sbtcrossproject.{crossProject, CrossType}
import sbtghactions.UseRef

//=============================================================================
//============================== build details ================================
//=============================================================================

addCommandAlias("github-gen", "githubWorkflowGenerate")
addCommandAlias("github-check", "githubWorkflowCheck")
Global / onChangedBuildSource := ReloadOnSourceChanges

val `Scala3.0.x` = "3.0.0-RC1"

//=============================================================================
//============================ publishing details =============================
//=============================================================================

ThisBuild / baseVersion  := "0.0.1"
ThisBuild / organization := "com.lorandszakacs"
ThisBuild / homepage     := Option(url("https://github.com/lorandszakacs/sprout"))

ThisBuild / publishFullName := "Loránd Szakács"

ThisBuild / scmInfo := Option(
  ScmInfo(
    browseUrl  = url("https://github.com/lorandszakacs/sprout"),
    connection = "git@github.com:lorandszakacs/sprout.git"
  )
)

/** I want my email. So I put this here. To reduce a few lines of code,
  * the sbt-spiewak plugin generates this (except email) from these two settings:
  * {{{
  * ThisBuild / publishFullName   := "Loránd Szakács"
  * ThisBuild / publishGithubUser := "lorandszakacs"
  * }}}
  */
ThisBuild / developers := List(
  Developer(
    id    = "lorandszakacs",
    name  = "Loránd Szakács",
    email = "lorand.szakacs@protonmail.com",
    url   = new java.net.URL("https://github.com/lorandszakacs")
  )
)

ThisBuild / startYear  := Option(2021)
ThisBuild / licenses   := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

//until we get to 1.0.0, we keep strictSemVer false
ThisBuild / strictSemVer              := false
ThisBuild / spiewakCiReleaseSnapshots := true
ThisBuild / spiewakMainBranches       := List("main")
ThisBuild / Test / publishArtifact    := false

ThisBuild / scalaVersion       := `Scala3.0.x`
ThisBuild / crossScalaVersions := List(`Scala3.0.x`)

//required for binary compat checks
ThisBuild / versionIntroduced := Map(
  `Scala3.0.x` -> "0.0.1"
)

//=============================================================================
//============================== Project details ==============================
//=============================================================================

val catsVersion            = "2.4.2"  // https://github.com/typelevel/cats/releases
val munitCatsEffectVersion = "0.13.1" // https://github.com/typelevel/munit-cats-effect/releases

lazy val root = project
  .in(file("."))
  .aggregate(
    sproutJVM,
    sproutJS
  )
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(SonatypeCiReleasePlugin)
  .settings(commonSettings)

lazy val sprout = crossProject(JSPlatform, JVMPlatform)
  .settings(commonSettings)
  .settings(dottyJsSettings(ThisBuild / crossScalaVersions))
  .jsSettings(
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )
  .settings(
    name := "sprout",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core"           % catsVersion  withSources (),
      "org.typelevel" %%% "munit-cats-effect-3" % munitCatsEffectVersion % Test withSources ()
    ),
    //required for munit, see: https://scalameta.org/munit/docs/getting-started.html#quick-start
    testFrameworks += new TestFramework("munit.Framework")
  )

lazy val sproutJVM = sprout.jvm.settings(
  javaOptions ++= Seq("-source", "1.8", "-target", "1.8")
)
lazy val sproutJS  = sprout.js

lazy val commonSettings = Seq(
  // Flag -source and -encoding set repeatedly
  // previous source flag set by one of the many plugins used
  scalacOptions := scalacOptions.value
    .filterNot(_.startsWith("-source:"))
    .filterNot(_.startsWith("-encoding"))
    .filterNot(_.startsWith("UTF-8"))
    .toSet //eliminate possible duplicates upstream
    .toSeq ++ Seq(
    "-encoding",
    "UTF-8",
    "-source:future",
    "-language:strictEquality"
    //"-explain-types",
    //"-explain"
  )
)
