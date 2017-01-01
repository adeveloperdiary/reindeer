package com.metlife.santa.core

import java.util

import com.metlife.santa.core.bean.{DonnerConfig}
import org.apache.spark.rdd.RDD


case class EntityData(name:String,base:collection.mutable.Map[String,String],
                      core:collection.mutable.Map[String,String],ext:collection.mutable.Map[String,String])

class Donner extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(classOf[DonnerConfig],file)
    this
  }

  def process() = {
    println("")
    println("=============== DONNER ( E3 ) ===================")
    println("")

    val _tempRDD=inputRDD.asInstanceOf[RDD[util.Map[String, AnyRef]]]

    val mapping=sc.broadcast[DonnerConfig](config.asInstanceOf[DonnerConfig])

    outputRDD=_tempRDD.map(row=>{


        val entityIterator=mapping.value.mapping.iterator()

        var entityDataList=collection.mutable.ListBuffer.empty[EntityData]

        while(entityIterator.hasNext){

          val entity=entityIterator.next()
          val dataAttributes=row.get(entity.sorEntityName).asInstanceOf[util.HashMap[String,AnyVal]]

          val base=collection.mutable.Map.empty[String,String]
          val core=collection.mutable.Map.empty[String,String]
          val ext=collection.mutable.Map.empty[String,String]

          val attributeIterator=entity.attributeMap.iterator()

          while(attributeIterator.hasNext){
            val attribute=attributeIterator.next()


            val data=dataAttributes.get(attribute.source).asInstanceOf[String]
            var result=data
            if(attribute.transform!=null && !attribute.transform.trim.equalsIgnoreCase("")){

              val transformations=attribute.transform.split("\\|")

              transformations.foreach(transform=>{

                result = transform match {
                  case "trim" => data.trim
                  case "uppercase" => data.toUpperCase
                  case "lowercase" => data.toLowerCase
                  case _           => data
                }
              })
            }

            if(attribute.dtype.equalsIgnoreCase("base")){
              base.put(attribute.name,result)
            }else if(attribute.dtype.equalsIgnoreCase("core")){
              core.put(attribute.name,result)
            }else{
              ext.put(attribute.name,result)
            }
          }
          entityDataList+=new EntityData(entity.entityName,base,core,ext)
        }

      entityDataList

    }).asInstanceOf[RDD[AnyRef]]

    outputRDD.collect.foreach(println)

  }

}
