import sbtcrossproject.{crossProject, CrossType}
import sbtghactions.UseRef

//=============================================================================
//============================== build details ================================
//=============================================================================

addCommandAlias("github-gen", "githubWorkflowGenerate")
addCommandAlias("github-check", "githubWorkflowCheck")
Global / onChangedBuildSource := ReloadOnSourceChanges

val Scala212  = "2.12.13"
val Scala213  = "2.13.5"
val Scala3RC1 = "3.0.0-RC1"
val Scala3RC2 = "3.0.0-RC2"

//=============================================================================
//============================ publishing details =============================
//=============================================================================

ThisBuild / baseVersion  := "0.0.2"
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
ThisBuild / spiewakCiReleaseSnapshots := false
ThisBuild / spiewakMainBranches       := List("main")
ThisBuild / Test / publishArtifact    := false

ThisBuild / scalaVersion       := Scala3RC1
ThisBuild / crossScalaVersions := List(Scala3RC2, Scala3RC1, Scala213, Scala212)

//required for binary compat checks
ThisBuild / versionIntroduced := Map(
  Scala212  -> "0.0.1",
  Scala213  -> "0.0.1",
  Scala3RC1 -> "0.0.1",
  Scala3RC2 -> "0.0.2"
)

//=============================================================================
//============================== Project details ==============================
//=============================================================================
// format: off
val catsVersion                = "2.5.0"      // https://github.com/typelevel/cats/releases
val munitCatsEffectVersion     = "1.0.1"      // https://github.com/typelevel/munit-cats-effect/releases
val shapelessVersion           = "2.3.3"      // used only for scala 2
// format: on

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
    ) ++ (if (isDotty.value) {
            Seq()
          }
          else {
            Seq(
              "com.chuusai" %%% "shapeless" % shapelessVersion withSources ()
            )
          }),
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
    .filterNot(_.startsWith("-source:")) ++
    (if (isDotty.value) {
       Seq(
         "-source:future",
         "-language:strictEquality"
       )
     }
     else {
       Seq()
     }),
  Compile / unmanagedSourceDirectories ++= {
    val major = if (isDotty.value) "-3" else "-2"
    List(CrossType.Pure, CrossType.Full).flatMap(
      _.sharedSrcDir(baseDirectory.value, "main").toList.map(f => file(f.getPath + major))
    )
  },
  Test / unmanagedSourceDirectories ++= {
    val major = if (isDotty.value) "-3" else "-2"
    List(CrossType.Pure, CrossType.Full).flatMap(
      _.sharedSrcDir(baseDirectory.value, "test").toList.map(f => file(f.getPath + major))
    )
  }
)
