package com.metlife.santa.core

import com.metlife.santa.config.DasherConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

class Dasher extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(classOf[DasherConfig],file)

    this
  }

  override def process() = {
    val objDasherConfig: DasherConfig = config.asInstanceOf[DasherConfig]

    conf = new SparkConf().setAppName(objDasherConfig.getName).setMaster(objDasherConfig.getMaster)
    sc = new SparkContext(conf)

    outputRDD=sc.textFile(objDasherConfig.getInput_url).asInstanceOf[RDD[AnyRef]]
  }

}