package com.metlife.santa.core


import com.metlife.santa.core.bean.DasherConfig
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

class Dasher extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(classOf[DasherConfig],file)

    this
  }

  override def process() = {
    val objDasherConfig = config.asInstanceOf[DasherConfig]

    Logger.getLogger("org").setLevel(Level.WARN)

    conf = new SparkConf().setAppName(objDasherConfig.name).setMaster(objDasherConfig.master)
    sc = new SparkContext(conf)

    outputRDD=sc.textFile(objDasherConfig.getInput_url).asInstanceOf[RDD[AnyRef]]
  }

}