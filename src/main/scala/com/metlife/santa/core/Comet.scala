package com.metlife.santa.core

import com.metlife.santa.core.bean.{CometConfig, Tables}
import org.apache.spark.rdd.RDD
import unicredit.spark.hbase._


class Comet extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(classOf[CometConfig],file)
    this
  }

  override def process() = {

    println("")
    println("=============== COMET ( HBASE ) ===================")
    println("")

    implicit val config = HBaseConfig(
      "hbase.zookeeper.quorum" -> "localhost"
    )

    val _tempRDD=inputRDD.asInstanceOf[RDD[List[EntityData]]]

    val tablesRDD=_tempRDD.flatMap(row=>row.toArray[EntityData])

    reindeerConfig.asInstanceOf[CometConfig].mapping.toArray().foreach(table=>{

      val tableMapping=table.asInstanceOf[Tables]

      tablesRDD.filter(entity=>{

        var retVal=false
        if(entity.name.equalsIgnoreCase(tableMapping.entityName))
          retVal=true

        retVal
      }).map({row=>

        val content = Map(
          "base" -> row.base,
          "core" -> row.core,
          "ext" -> row.ext
        )
        row.key->content

      }).toHBase(tableMapping.tableName)

    })

  }

}
