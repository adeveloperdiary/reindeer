name := "reindeer"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.2"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.2"
//libraryDependencies += "net.sf.cb2java" %% "cb2java" % "5.3"
unmanagedJars in Compile += file("/Users/abhisekjana/spark/reindeer/libs/cb2java-5.3.jar")