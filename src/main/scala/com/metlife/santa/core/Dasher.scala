package com.metlife.santa.core


import com.metlife.santa.core.bean.DasherConfig


import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


class Dasher extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(classOf[DasherConfig],file)

    this
  }

  override def process() = {

    val objDasherConfig = reindeerConfig.asInstanceOf[DasherConfig]
    spark = SparkSession
      .builder
      .appName(objDasherConfig.name)
      .master(objDasherConfig.master)
      .getOrCreate()


    println("")
    println("=============== DASHER ( Driver ) ===================")
    println("")

    outputRDD=spark.sparkContext.textFile(objDasherConfig.getInput_url).asInstanceOf[RDD[AnyRef]]

    outputRDD.collect().foreach(println)


  }

}