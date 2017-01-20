package com.metlife.santa.core

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.metlife.santa.core.bean.{CupidConfig, VAttribute, Vertex}
import org.apache.spark.rdd.RDD
import play.api.libs.json.{JsArray, JsUndefined}
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.libs.ws.ahc.AhcWSClient
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Cupid extends ReindeerBase{

  override def init(file: String): ReindeerBase = {
    initReindeer(classOf[CupidConfig],file)
    this
  }

  override def process(): Unit = {


    println("")
    println("=============== CUPID ( Titan ) ===================")
    println("")


    val mapping=spark.sparkContext.broadcast[CupidConfig](reindeerConfig.asInstanceOf[CupidConfig])

    val _tempRDD=inputRDD.asInstanceOf[RDD[List[EntityData]]]

    _tempRDD.flatMap(row=>row.toArray[EntityData]).foreachPartition(rows=>{
      import scala.concurrent.ExecutionContext.Implicits._

      def getProperties(wsClient: WSClient,data:EntityData): Future[WSResponse] = {

        var finalQuery:String=""
        mapping.value.vertex.toArray.toList.foreach(v=>{

          val vertex=v.asInstanceOf[Vertex]
          var query:String="__.as('"+vertex.label+"').has(label,'"+vertex.label+"')"

          vertex.keys.toArray().toList.foreach(attr=>{

            val vAttr=attr.asInstanceOf[VAttribute]

            val attrValue=vAttr.dtype match {

              case "base"=> data.base.get(vAttr.name).get
              case "core"=> data.core.get(vAttr.name).get
              case "ext" => data.ext.get(vAttr.name).get
              case _     => ""

            }
            query+=",__.as('"+vertex.label+"').has('"+vAttr.name+"','"+attrValue+"')"
          })

          if(!finalQuery.equalsIgnoreCase(""))
            finalQuery+=","

          finalQuery+="match("+query+")"
        })

        println(finalQuery)
        wsClient.url(mapping.value.config.url+"g.V().union("+finalQuery+")").get()
      }

      def getResult(response:WSResponse):Option[JsArray]={
        println("=======================")
        println(response.body)
        println("=======================")

        if(response.json!=None){
          val json=response.json
          if(!(json \ "message").isInstanceOf[JsUndefined]){
            println("Error:"+ (json \"message").get)
          }else{
            val data=(json \ "result" \ "data").get.asInstanceOf[JsArray]
            return Option(data)
          }
        }
        None
      }

      rows.foreach(entity=>{
        implicit val system = ActorSystem()
        implicit val materializer = ActorMaterializer()
        val wsClient = AhcWSClient()

        val service=getProperties(wsClient,entity)
          .andThen { case _ => wsClient.close() }
          .andThen { case _ => system.terminate() }

        service.onComplete({
          case Success(response) => {
            val size=getResult(response).get.value.size
            if(size>0){

            }else{

            }
          }
          case Failure(exception) => {
            println("Error")
          }
        })


      })

    })

  }
}



