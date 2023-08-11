name := """QnA-app"""
organization := "com.vub"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.11"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
scalacOptions ++= Seq("-target:jvm-11")

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.vub.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.vub.binders._"
