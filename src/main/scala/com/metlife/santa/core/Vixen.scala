package com.metlife.santa.core

import java.util

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class Vixen extends ReindeerBase{

  def init(file: String) = {
    initReindeer(classOf[AnyVal],file)
  }

  def process(inputRDD:RDD[util.Map[String, AnyRef]],sc:SparkContext) = {

    val validation=sc.broadcast[util.ArrayList[AnyVal]](config.asInstanceOf[util.ArrayList[AnyVal]])

    inputRDD.filter(x=>{

      var flag=true
      val it=validation.value.iterator()
      while(it.hasNext){
        val element=it.next().asInstanceOf[util.HashMap[String,AnyVal]]
        if(element.get("children")!=null){

          val dataAttr=x.get(element.get("name")).asInstanceOf[util.HashMap[String,AnyVal]]

          val it1=element.get("children").asInstanceOf[util.ArrayList[AnyVal]].iterator
          while(it1.hasNext){
            val attributes=it1.next().asInstanceOf[util.HashMap[String,AnyVal]]

            val data:String=dataAttr.get(attributes.get("name")).asInstanceOf[String]

            if(attributes.get("regex")!=null
              && !attributes.get("regex").asInstanceOf[String].equalsIgnoreCase("")){
              //TODO - RegEx

              if(!data.matches(attributes.get("regex").asInstanceOf[String])){
                println("Regex Validation failed : ["+ dataAttr.toString+"] ["+data+"] ["+attributes.get("regex")+"]")

                flag=false

              }
            }
          }
        }else{
          //TODO - Validation
        }
      }


      flag
    })
    .collect.foreach(println)
  }
}
