name := "reindeer"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.2"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.2"

//libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.2"




unmanagedJars in Compile += file("./libs/cb2java-5.3.jar")