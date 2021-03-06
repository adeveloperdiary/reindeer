package com.metlife.santa.core

import java.io.{IOException, InputStream}
import java.util.Properties

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import org.codehaus.jackson.map.ObjectMapper


trait Reindeer {
  protected var prop: Properties = new Properties()
  protected var reindeerConfig: Any = null

  protected var conf: SparkConf = null
  protected var spark: SparkSession = null


  protected var inputRDD:RDD[AnyRef]=null
  protected var outputRDD:RDD[AnyRef]=null

  def getSparkSession():SparkSession


  def init(file: String):ReindeerBase
  def chain(previous:Reindeer ):Reindeer
  def process()

  def getInputRDD():RDD[AnyRef]
  def getOutputRDD():RDD[AnyRef]
}


abstract class ReindeerBase extends Reindeer{

  @throws[IOException]
  def initReindeer(file: String) {
    val fileName="santa."+file+".properties"
    this.prop.load(this.getClass.getClassLoader.getResourceAsStream(fileName))
  }

  @throws[IOException]
  def initReindeer(className: Class[_], file: String) {


    val fileName="santa."+file+".config.json"
    val mapper = new ObjectMapper
    var in: InputStream = null
    try {

      //mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false)
      in = getClass.getClassLoader.getResourceAsStream(fileName)
      reindeerConfig = mapper.readValue(in, className)
    } finally {
      in.close
    }
  }

  override def getInputRDD():RDD[AnyRef]={
    inputRDD
  }

  override def getOutputRDD():RDD[AnyRef]={
    outputRDD
  }

  override def getSparkSession():SparkSession ={
    spark
  }

  override def chain(previous: Reindeer): Reindeer = {
    spark=previous.getSparkSession()
    inputRDD=previous.getOutputRDD()
    this
  }
}



