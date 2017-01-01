package com.metlife.santa.core

import com.metlife.santa.config.DonnerConfig
import org.apache.spark.SparkContext

class Donner extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(classOf[DonnerConfig],file)
    this
  }

  def process() = {


  }

  override def getSparkContext():SparkContext ={
    sc
  }

  override def chain(previous: Reindeer): Unit = {
    sc=previous.getSparkContext()
  }

}
