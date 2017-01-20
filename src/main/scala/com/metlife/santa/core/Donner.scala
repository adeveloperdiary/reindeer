package com.metlife.santa.core

import java.util

import com.metlife.santa.core.bean.DonnerConfig
import org.apache
import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import unicredit.spark.hbase._


case class EntityData(name:String,key:String,base:Map[String,String],
                      core:Map[String,String],ext:Map[String,String])

case class KeyValuePair(key:String,value:String)

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

    val mapping=spark.sparkContext.broadcast[DonnerConfig](reindeerConfig.asInstanceOf[DonnerConfig])

    implicit val config = HBaseConfig(
      "hbase.zookeeper.quorum" -> "localhost"
    )

    val loopupIterator=mapping.value.lookups.iterator()
    val localLookup=collection.mutable.Map.empty[String,Dataset[KeyValuePair]]

    while(loopupIterator.hasNext){
      val loopup=loopupIterator.next()

      val columns = Map(
        "d" -> Set("key", "value")
      )

      val spark=this.spark
      import spark.implicits._
      val lookupDS=spark.sparkContext.hbase[String](loopup.table, columns).map(row=>{

        val d=row._2.get("d").get
        val keyVal=new KeyValuePair(d.get("key").get,d.get("value").get)

        keyVal

      }).toDS()

      localLookup.put(loopup.name,lookupDS)

    }

    val lookup=spark.sparkContext.broadcast[Map[String,Dataset[KeyValuePair]]](localLookup.toMap)


    outputRDD=_tempRDD.map(row=>{


        val entityIterator=mapping.value.mapping.iterator()

        var entityDataList=collection.mutable.ListBuffer.empty[EntityData]

        var key:String=""

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

                if(transform.startsWith("lookup:")){
                  val loopupName=transform.split("^lookup:")(1).trim
                  val ds=lookup.value.get(loopupName).get

                  result=ds.filter(ds("key")=== data).take(1)(0).value

                }
              })
            }

            if(attribute.dtype.equalsIgnoreCase("base")){
              base.put(attribute.name,result)
            }else if(attribute.dtype.equalsIgnoreCase("core")){
              core.put(attribute.name,result)
            }else if(attribute.dtype.equalsIgnoreCase("ext")){
              ext.put(attribute.name,result)
            }else{
              key=Math.abs(result.hashCode).toString
            }
          }
          entityDataList+=new EntityData(entity.entityName,key,base.toMap,core.toMap,ext.toMap)
        }

      entityDataList.toList

    }).asInstanceOf[RDD[AnyRef]]

    //outputRDD.collect.foreach(println)

  }
}
