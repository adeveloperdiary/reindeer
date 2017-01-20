name := "reindeer"

version := "1.0"

scalaVersion := "2.11.8"

val hbaseVersion = "1.0.0-cdh5.5.2"

dependencyOverrides ++= Set("com.fasterxml.jackson.core" % "jackson-databind" % "2.6.4")
dependencyOverrides ++= Set("com.google.guava" % "guava" % "16.0")

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.0.2"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.0.2"

//libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.7.2"

libraryDependencies +=  "org.apache.hbase" % "hbase-common" % hbaseVersion excludeAll(ExclusionRule(organization = "javax.servlet", name="javax.servlet-api"), ExclusionRule(organization = "org.mortbay.jetty", name="jetty"), ExclusionRule(organization = "org.mortbay.jetty", name="servlet-api-2.5"))

libraryDependencies +=  "org.apache.hbase" % "hbase-client" % hbaseVersion excludeAll(ExclusionRule(organization = "javax.servlet", name="javax.servlet-api"), ExclusionRule(organization = "org.mortbay.jetty", name="jetty"), ExclusionRule(organization = "org.mortbay.jetty", name="servlet-api-2.5"))

libraryDependencies +=  "org.apache.hbase" % "hbase-server" % hbaseVersion excludeAll(ExclusionRule(organization = "javax.servlet", name="javax.servlet-api"), ExclusionRule(organization = "org.mortbay.jetty", name="jetty"), ExclusionRule(organization = "org.mortbay.jetty", name="servlet-api-2.5"))

libraryDependencies += "org.apache.hbase" % "hbase-protocol" % hbaseVersion
libraryDependencies += "org.apache.hbase" % "hbase-hadoop-compat" % hbaseVersion


libraryDependencies += "org.json4s" % "json4s-jackson_2.11" % "3.5.0"
libraryDependencies += "eu.unicredit" % "hbase-rdd_2.11" % "0.8.0"

//libraryDependencies += "com.tinkerpop.rexster" % "rexster-protocol" % "2.3.0"

libraryDependencies += "com.typesafe.play" % "play-ws_2.11" % "2.5.10"


resolvers ++= Seq(
  "Cloudera repos" at "https://repository.cloudera.com/artifactory/cloudera-repos",
  "Cloudera releases" at "https://repository.cloudera.com/artifactory/libs-release",
  "Apache Repository" at "https://repository.apache.org/content/repositories/releases/",
  "Cloudera repo" at "https://repository.cloudera.com/content/repositories/releases/",
  "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
  "Apache public" at "https://repository.apache.org/content/groups/public/"
)

unmanagedJars in Compile += file("./libs/cb2java-5.3.jar")

