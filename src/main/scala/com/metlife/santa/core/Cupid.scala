package com.metlife.santa.core

import com.tinkerpop.rexster.client.RexsterClientFactory
import org.apache.spark.rdd.RDD


class Cupid extends ReindeerBase{
  override def init(file: String): ReindeerBase = {
    this
  }

  override def process(): Unit = {


    println("")
    println("=============== CUPID ( Titan ) ===================")
    println("")


    val _tempRDD=inputRDD.asInstanceOf[RDD[List[EntityData]]]

    _tempRDD.flatMap(row=>row.toArray[EntityData]).foreachPartition(rows=>{

      val client = RexsterClientFactory.open("localhost", "graph")

      rows.map(entity=>{

        val v1 = "v1 = g.addVertex([claimNum:\""+entity.base.get("ClaimNumber").get+"\" ])"
        val v2 = "v2 = g.addVertex([name:\""+entity.ext.get("CustomerName").get+"\" ])"
        val e = "e1= g.addEdge(v1,v2,\"has\",[status: \""+entity.core.get("Status").get+"\"])"
        val out = client.execute(v1+";"+v2+";"+e)
        print(out.toString)

      }).length

      client.close()
    })

  }
}
