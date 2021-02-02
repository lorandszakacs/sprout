import sbtcrossproject.{crossProject, CrossType}
import sbtghactions.UseRef

addCommandAlias("github-gen", "githubWorkflowGenerate")
addCommandAlias("github-check", "githubWorkflowCheck")

val Scala3 = "3.0.0-M3"

enablePlugins(SonatypeCiReleasePlugin)

ThisBuild / publishFullName   := "Loránd Szakács"
ThisBuild / publishGithubUser := "lorandszakacs"
ThisBuild / organization      := "com.lorandszakacs"
ThisBuild / baseVersion       := "0.1"

ThisBuild / scalaVersion       := Scala3
ThisBuild / crossScalaVersions := Seq(Scala3)

ThisBuild / versionIntroduced        := Map(
  Scala3 -> "0.1.0"
)

ThisBuild / githubWorkflowSbtCommand := "csbt"

ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.8", "adopt@1.11")

ThisBuild / githubWorkflowBuild       := Seq(
  WorkflowStep.Sbt(List("test"), name                   = Some("Test")),
  WorkflowStep.Sbt(List("mimaReportBinaryIssues"), name = Some("Binary Compatibility Check"))
)

ThisBuild / spiewakCiReleaseSnapshots := true
ThisBuild / spiewakMainBranches       := Seq("main")

ThisBuild / githubWorkflowAddedJobs ++= Seq(
  //FIXME: enable once scala 3 is fully supported by scalafmt
  // WorkflowJob(
  //   "scalafmt",
  //   "Scalafmt",
  //   githubWorkflowJobSetup.value.toList ::: List(
  //     WorkflowStep.Sbt(List("scalafmtCheckAll"), name = Some("Scalafmt"))
  //   ),
  //   scalas = crossScalaVersions.value.toList
  // )
)

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

val catsVersion            = "2.3.1"    // https://github.com/typelevel/cats/releases
val catsEffectVersion      = "3.0.0-M5" // https://github.com/typelevel/cats-effect/releases
val munitCatsEffectVersion = "0.13.0"   // https://github.com/typelevel/munit-cats-effect/releases

Global / onChangedBuildSource := ReloadOnSourceChanges
//required for munit, see: https://scalameta.org/munit/docs/getting-started.html#scalajs-setup
Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }

lazy val root = project
  .in(file("."))
  .aggregate(
    sproutJVM,
    sproutJS,
    `sprout-effectJVM`,
    `sprout-effectJS`
  )
  .enablePlugins(NoPublishPlugin)
  .settings(commonSettings, releaseSettings)

lazy val sprout = crossProject(JSPlatform, JVMPlatform)
  .settings(commonSettings)
  .settings(releaseSettings)
  .settings(
    name := "sprout",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core"           % catsVersion  withSources (),
      "org.typelevel" %%% "munit-cats-effect-3" % munitCatsEffectVersion % Test withSources ()
    )
  )

lazy val sproutJVM = sprout.jvm

lazy val sproutJS = sprout.js.settings(
  test in Test := {} //FIXME: temporary until I can figure out the munit test Framework on JS bit
)

lazy val `sprout-effect` = crossProject(JSPlatform, JVMPlatform)
  .settings(commonSettings)
  .settings(releaseSettings)
  .settings(
    name := "sprout-effect",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats-core"           % catsVersion       withSources (),
      "org.typelevel" %%% "cats-effect"         % catsEffectVersion withSources (),
      "org.typelevel" %%% "munit-cats-effect-3" % munitCatsEffectVersion      % Test withSources ()
    )
  )

lazy val `sprout-effectJVM` = `sprout-effect`.jvm

lazy val `sprout-effectJS` = `sprout-effect`.js.settings(
  test in Test := {} //FIXME: temporary until I can figure out the munit test Framework on JS bit
)

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(),
  //required for munit, see: https://scalameta.org/munit/docs/getting-started.html#quick-start
  testFrameworks += new TestFramework("munit.Framework"),
  //ensure that we have only a dotty project,
  //remove the filter periodically to see if sbt warns about:
  //  Flag -source set repeatedly
  //previous source flag set by one of the many plugins used
  scalacOptions := scalacOptions.value.filterNot(_.startsWith("-source:")) ++ Seq(
    "-source:3.1",
    "-explain-types",
    "-explain"
  )
)

lazy val releaseSettings = {
  Seq(
    publishArtifact in Test := false,
    scmInfo                 := Some(
      ScmInfo(
        url("https://github.com/lorandszakacs/sprout"),
        "git@github.com:lorandszakacs/sprout.git"
      )
    ),
    homepage                := Some(url("https://github.com/lorandszakacs/sprout")),
    licenses                := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    startYear               := Some(2021),
    developers              := List(
      Developer(
        "lorandszakacs",
        "Loránd Szakács",
        "lorand.szakacs@protonmail.com",
        new java.net.URL("https://github.com/lorandszakacs")
      )
    )
  )
}
