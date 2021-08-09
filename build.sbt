import sbt.Keys.libraryDependencies

name := "Sapling"
version := "1.0"
scalaVersion := "2.11.12"
val spinalVersion = "1.4.3"

libraryDependencies ++= Seq(
  "com.github.spinalhdl" % "spinalhdl-core_2.11" % spinalVersion,
  "com.github.spinalhdl" % "spinalhdl-lib_2.11" % spinalVersion,
  "org.scalactic" %% "scalactic" % "3.2.5",
  "org.scalatest" %% "scalatest" % "3.2.5" % "test",
  "junit" % "junit" % "4.12" % Test,
  "com.novocode" % "junit-interface" % "0.11" % Test exclude("junit", "junit-dep"),
  compilerPlugin("com.github.spinalhdl" % "spinalhdl-idsl-plugin_2.11" % spinalVersion)
)

fork := true
EclipseKeys.withSource := true


//ThisBuild / version := "1.0"
//ThisBuild / scalaVersion := "2.11.12"
////ThisBuild / organization := "org.example"
//
//val spinalVersion = "1.6.0"
//val spinalCore = "com.github.spinalhdl" %% "spinalhdl-core" % spinalVersion
//val spinalLib = "com.github.spinalhdl" %% "spinalhdl-lib" % spinalVersion
//val spinalIdslPlugin = compilerPlugin("com.github.spinalhdl" %% "spinalhdl-idsl-plugin" % spinalVersion)
//
//lazy val mylib = (project in file("."))
//  .settings(
//    name := "SpinalTemplateSbt",
//    libraryDependencies ++= Seq(spinalCore, spinalLib, spinalIdslPlugin)
//  )
//
//fork := true
