package com.metlife.santa.core

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.metlife.santa.core.bean.{CupidConfig, Relationships, VAttribute}
import org.apache.spark.rdd.RDD
import play.api.libs.json.{JsArray, JsUndefined, JsValue}
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

      def getValue(vAttr:VAttribute,data:EntityData):String={

        val attrValue=vAttr.dtype match {

          case "base"=> data.base.get(vAttr.name).get
          case "core"=> data.core.get(vAttr.name).get
          case "ext" => data.ext.get(vAttr.name).get
          case _     => ""

        }
        attrValue
      }

      def getAttribQuery(label:String,keys:List[AnyRef],data:EntityData):String={

        var query:String="__.as('"+label+"').has(label,'"+label+"')"
        keys.foreach(attr=>{
          val vAttr=attr.asInstanceOf[VAttribute]
          query+=",__.as('"+label+"').has('"+vAttr.name+"','"+getValue(vAttr,data)+"')"
        })
        "match("+query+")"
      }

      def createVertixQuery(label:String,keys:List[AnyRef],attr:List[AnyRef],dirty:Boolean,data:EntityData):String={
        var query="label,'"+label+"'"

        keys.foreach(key=>{
          val vAttr=key.asInstanceOf[VAttribute]
          query+=",'"+vAttr.name+"','"+getValue(vAttr,data)+"'"
        })

        attr.foreach(attr=>{
          val vAttr=attr.asInstanceOf[VAttribute]
          query+=",'"+vAttr.name+"','"+getValue(vAttr,data)+"'"
        })

        if(dirty){
          query+=",'dirty','true'"
        }

        "addV("+query+").as('"+label+"')"
      }

      def createEdgeQuery(idMap:Map[String,JsValue]):String={

        var query:String=""
        mapping.value.graph.relationships.toArray.toList.foreach(relationships=>{
          val relationship=relationships.asInstanceOf[Relationships]
          query+="g.V("+idMap.get(mapping.value.graph.entity_name).get+").next().addEdge('"+relationship.relationship.name+"', g.V("+idMap.get(relationship.related_entity.name).get+").next());"
        })
        query
      }

      def submitQuery(wsClient: WSClient,query:String):Future[WSResponse]={

        val URL=mapping.value.config.url+query
        println(URL)
        wsClient.url(URL).get()
      }

      def submitPostQuery(wsClient: WSClient,query:String):Future[WSResponse]={
        println(query)
        val url=mapping.value.config.url

        wsClient.url(url.substring(0,url.indexOf("?")-1))
          .post("{    \"gremlin\": \""+query+"\"}")
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

        var queryAllAttrib=getAttribQuery(mapping.value.graph.entity_name,mapping.value.graph.keys.toArray.toList,entity)

        mapping.value.graph.relationships.toArray.toList.foreach(relationship=>{
          val related_entity=relationship.asInstanceOf[Relationships].related_entity
          queryAllAttrib+=","+getAttribQuery(related_entity.name,related_entity.keys.toArray.toList,entity)
        })

        submitQuery(wsClient,"g.V().union("+queryAllAttrib+")")
          .andThen { case _ => wsClient.close() }
          .andThen { case _ => system.terminate() }
          .onComplete({
          case Success(response) => {

            val respData=getResult(response).get

            if(respData.value.size>0){
              //Some or All Entities are available
              //TODO - Throw Error in case more than one node present in db

              val nodeIdMap=collection.mutable.Map.empty[String,JsValue]

              respData.value.foreach(value=>{
                val str=value.toString()
                val label=str.substring(2).substring(0,str.indexOf(":")-3)
                val id=value\label\"id"
                nodeIdMap.put(label,id.get)
              })

              // Some / All Missing Vertex
              // Add the missing vertex first

              //var createVertexList=collection.mutable.ListBuffer.empty[String]
              var queryVertex=""
              if(!nodeIdMap.contains(mapping.value.graph.entity_name)){
                //createVertexList+=mapping.value.graph.entity_name
                queryVertex=createVertixQuery(mapping.value.graph.entity_name,
                  mapping.value.graph.keys.toArray.toList,mapping.value.graph.attributes.toArray.toList,false,entity)

              }

              mapping.value.graph.relationships.toArray.toList.foreach(relationships=>{
                val related_entity=relationships.asInstanceOf[Relationships].related_entity

                if(!nodeIdMap.contains(related_entity.name)){
                  //createVertexList+=related_entity.name

                  if(!queryVertex.equalsIgnoreCase(""))
                    queryVertex+="."
                  queryVertex+=createVertixQuery(related_entity.name,related_entity.keys.toArray.toList,
                    related_entity.attributes.toArray.toList,related_entity.dirty,entity)
                }
              })

              println(queryVertex)

              val wsClient1 = AhcWSClient()
              submitQuery(wsClient1,"g."+queryVertex)
                .andThen { case _ => wsClient1.close() }
                .andThen { case _ => system.terminate() }
                .onComplete({
                  case Success(response) => {

                    //Update the existing vertex

                    var queryFinal:String=""

                    if(nodeIdMap.contains(mapping.value.graph.entity_name)){
                      mapping.value.graph.attributes.toArray.toList.foreach(attr=>{

                        val vAttr=attr.asInstanceOf[VAttribute]

                        if(!queryFinal.equalsIgnoreCase(""))
                          queryFinal+="."
                        queryFinal+="property('"+vAttr.name+"','"+getValue(vAttr,entity)+"')"

                      })
                      queryFinal="g.V("+nodeIdMap.get(mapping.value.graph.entity_name).get+")."+queryFinal
                    }



                    mapping.value.graph.relationships.toArray.toList.foreach(relationships=>{
                      var query:String=""
                      val related_entity=relationships.asInstanceOf[Relationships].related_entity

                      if(nodeIdMap.contains(related_entity.name)){

                        related_entity.attributes.toArray.toList.foreach(attr=>{

                          val vAttr=attr.asInstanceOf[VAttribute]

                          if(!query.equalsIgnoreCase(""))
                            query+="."

                          query+="property('"+vAttr.name+"','"+getValue(vAttr,entity)+"')"

                        })

                        if(!query.equalsIgnoreCase(""))
                          query="g.V("+nodeIdMap.get(related_entity.name).get+")."+query

                        if(!queryFinal.equalsIgnoreCase("")){
                          queryFinal+=";"
                        }

                        queryFinal+=query
                      }
                    })
                    val wsClient2 = AhcWSClient()
                    submitPostQuery(wsClient2,queryFinal)
                      .andThen { case _ => wsClient2.close() }
                      .andThen { case _ => system.terminate() }
                      .onComplete({
                        case Success(response) => {

                          //Create the Edges



                        }
                        case Failure(exception) => {
                          println(exception)
                        }
                      })

                  }
                  case Failure(exception) => {
                    println(exception)
                  }
                })
            }else{
              //None of the Entities are available.
              //TODO: Create Vertex, Create related entity, mark it as dirty if needed, Create Edge

              var queryVertex=createVertixQuery(mapping.value.graph.entity_name,
                mapping.value.graph.keys.toArray.toList,mapping.value.graph.attributes.toArray.toList,false,entity)

              mapping.value.graph.relationships.toArray.toList.foreach(relationship=>{
                val related_entity=relationship.asInstanceOf[Relationships].related_entity
                queryVertex+="."+createVertixQuery(related_entity.name,related_entity.keys.toArray.toList,
                  related_entity.attributes.toArray.toList,related_entity.dirty,entity)
              })
              val wsClient1 = AhcWSClient()
              submitQuery(wsClient1,"g."+queryVertex)
                .andThen { case _ => wsClient1.close() }
                .andThen { case _ => system.terminate() }
                .onComplete({
                case Success(response) => {
                  val wsClient2 = AhcWSClient()
                  submitQuery(wsClient2,"g.V().union("+queryAllAttrib+")")
                    .andThen { case _ => wsClient2.close() }
                    .andThen { case _ => system.terminate() }
                    .onComplete({
                    case Success(response) => {
                      //TODO Get Edge IDs and create the Edges.

                      val nodeIdMap=collection.mutable.Map.empty[String,JsValue]
                      getResult(response).get.value.foreach(value=>{
                        val str=value.toString()
                        val label=str.substring(2).substring(0,str.indexOf(":")-3)
                        val id=value\label\"id"

                        nodeIdMap.put(label,id.get)
                      })
                      val wsClient3 = AhcWSClient()
                      submitPostQuery(wsClient3,createEdgeQuery(nodeIdMap.toMap))
                        .andThen { case _ => wsClient3.close() }
                        .andThen { case _ => system.terminate() }
                        .onComplete({
                          case Success(response) => {

                          }
                          case Failure(exception) => {
                            println(exception)
                          }
                        })
                    }
                    case Failure(exception) => {
                      println(exception)
                    }
                  })
                }
                case Failure(exception) => {
                  println(exception)
                }
              })
            }
          }
          case Failure(exception) => {
            println(exception)
          }
        })
      })
    })
  }
}

//http://localhost:8182/?gremlin=graph.traversal().addV(%27name%27,%27abhisek%27,label,%27Person%27)
//http://localhost:8182/?gremlin=graph.traversal().addV('number','2',label,'Claim','status','pending') 4224 40964096
//http://localhost:8182/?gremlin=graph.traversal().addV("name","john doe",label,"Party") 4312
//http://localhost:8182/?gremlin=graph.traversal().V(4224).next().addEdge('has', g.V(4312).next())
//http://localhost:8182/?gremlin=g.V().hasLabel('Claim').has('number','7866865')
//http://localhost:8182/?gremlin=g.V().hasLabel('Claim').has('status','pending').properties()
//http://localhost:8182/?gremlin=graph.traversal().addV(label,'Dummy')

//http://localhost:8182/?gremlin=graph.traversal().addV('ClaimNumber','00001111',label,'Claim','Status','pending','ClaimTyp e','DAN')
//http://localhost:8182/?gremlin=graph.traversal().addV('CustomerName','jane doe',label,'Party')


/*


g.V().union(V(41017456).next().addEdge('knows', g.V(53432).next()),V(41017456).next().addEdge('knows', g.V(53432).next()))

g.V(41017456).as('Claim').V(53432).as('Party').union(__.as('Claim'))

gremlin> :> g.addV()
==>v[3]
gremlin> :> g.addV()
==>v[4]
gremlin> :> g.V(3).next().addEdge('knows', g.V(4).next())
==>e[5][3-knows->4]
gremlin> :> g.E(5) #no reply
gremlin> :> g.V(3).next().addEdge('knows', g.V(4).next(), T.id, 999)
==>e[999][3-knows->4]
gremlin> :> g.E(999)
==>e[999][3-knows->4]

g.V().match(__.as('a').has(label,'Claim'),__.as('a').has('status', 'pending'),__.as('a').has('number', '2'))
g.V().match(__.as('a').has(label,'Claim'),__.as('a').has('status', 'pending'))
g.V().as('a').match(__.as('a').has(label,'Claim'),or(__.as('b').has(label,'Claim')))

//Use this
g.V().union(match(__.as('a').has(label,'Claim'),__.as('a').has('status','pending')),match(__.as('b').has(label,'Party')))

g.V().union(match(__.as('a').has(label,'Claim'),__.as('a').has('status','pending')))


g.V().match(__.as('a').has(label,'Claim'),__.as('a').has('status', 'pending'),__.as('a').has('number', '2'),__.as('b').has(label,'Party'))

http://localhost:8182/?gremlin=g.V().union(addV(label,'Claim','ClaimNumber','00002221','Status','pending','ClaimType','DBN'),addV(label,'Party','CustomerName','john doe'))


http://localhost:8182/?gremlin=g.V(53392).union(property('ClaimType','William'),property('name','abhisek'))
http://localhost:8182/?gremlin=g.V(53392).property('ClaimType','William').property('name','abhisek')

*/




