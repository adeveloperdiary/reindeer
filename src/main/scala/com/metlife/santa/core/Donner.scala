package com.metlife.santa.core

import java.util

import com.metlife.santa.core.bean.DonnerConfig
import org.apache.spark.rdd.RDD



class Donner extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(classOf[DonnerConfig],file)
    this
  }

  def process() = {


    val _tempRDD=inputRDD.asInstanceOf[RDD[util.Map[String, AnyRef]]]

    val mapping=sc.broadcast(config.asInstanceOf[DonnerConfig])

    outputRDD=_tempRDD.map(row=>{
        val entityIterator=mapping.value.getMapping.iterator()
        while(entityIterator.hasNext){
          val entity=entityIterator.next()
          val dataAttributes=row.get(entity.getSorEntityName).asInstanceOf[util.HashMap[String,AnyVal]]
          println(dataAttributes.toString)
        }

      (row)

    }).asInstanceOf[RDD[AnyRef]]


    outputRDD.collect.foreach(println)

  }

}
