import sbtcrossproject.{crossProject, CrossType}
import sbtghactions.UseRef

//=============================================================================
//============================== build details ================================
//=============================================================================

addCommandAlias("format", ";scalafmtSbt;scalafmtConfig;scalafmtAll")

Global / onChangedBuildSource := ReloadOnSourceChanges

val Scala212 = "2.12.13"
val Scala213 = "2.13.5"
val Scala3   = "3.0.0"

//=============================================================================
//============================ publishing details =============================
//=============================================================================

ThisBuild / baseVersion  := "0.0.4"
ThisBuild / organization := "com.lorandszakacs"
ThisBuild / homepage     := Option(url("https://github.com/lorandszakacs/sprout"))

ThisBuild / publishFullName := "Loránd Szakács"

ThisBuild / scmInfo := Option(
  ScmInfo(
    browseUrl  = url("https://github.com/lorandszakacs/sprout"),
    connection = "git@github.com:lorandszakacs/sprout.git"
  )
)

/** I want my email. So I put this here. To reduce a few lines of code, the sbt-spiewak plugin generates this (except
  * email) from these two settings:
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

ThisBuild / scalaVersion       := Scala3
ThisBuild / crossScalaVersions := List(Scala3, Scala213, Scala212)

//required for binary compat checks
ThisBuild / versionIntroduced := Map(
  Scala212    -> "0.0.1",
  Scala213    -> "0.0.1",
  "3.0.0-RC1" -> "0.0.1",
  "3.0.0-RC2" -> "0.0.2",
  "3.0.0-RC3" -> "0.0.3",
  Scala3      -> "0.0.4"
)

//=============================================================================
//============================== Project details ==============================
//=============================================================================
// format: off
val catsV        = "2.6.1"      // https://github.com/typelevel/cats/releases
val munitV       = "0.7.26"     // https://github.com/scalameta/munit/releases
val shapelessV   = "2.3.6"      // https://github.com/milessabin/shapeless/releases
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
      // format: off
      "org.typelevel" %%% "cats-core"  % catsV         withSources(),
      "org.scalameta" %%% "munit"      % munitV % Test withSources()
      // format: on
    ) ++ (if (isDotty.value)
            Seq.empty
          else
            Seq(
              "com.chuusai" %%% "shapeless" % shapelessV withSources ()
            ))
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
