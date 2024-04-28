lazy val scala2V = "2.13.13"
lazy val scala3V = "3.4.1"

lazy val javaProj = project
  .settings(
    scalaVersion := scala3V
  )

//lazy val before = project
//  .settings(
//    scalaVersion := scala3V
//  )
//
//lazy val after = project
//  .settings(
//    scalaVersion := scala3V
//  )

lazy val transform = project
  .settings(
    scalaVersion := scala2V,
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "os-lib" % "0.10.0",
      "org.scalameta" %% "scalameta" % "4.9.3",
      "org.eclipse.jgit" % "org.eclipse.jgit" % "6.9.0.202403050737-r",
    ),
  )