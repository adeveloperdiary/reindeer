package com.metlife.santa.core

import java.util

import org.apache.spark.rdd.RDD

class Vixen extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(classOf[AnyVal],file)
    this
  }

  def process() = {

    println("")
    println("=============== VIXEN ( D2 ) ===================")
    println("")

    val _tempRDD=inputRDD.asInstanceOf[RDD[util.Map[String, AnyRef]]]

    //TODO - Convert to Object from Map
    val validation=sc.broadcast[util.ArrayList[AnyVal]](config.asInstanceOf[util.ArrayList[AnyVal]])

    outputRDD=_tempRDD.filter(row=>{

      var flag=true
      val itValidationRoot=validation.value.iterator()
      while(itValidationRoot.hasNext){
        val validationEntity=itValidationRoot.next().asInstanceOf[util.HashMap[String,AnyVal]]

        if(validationEntity.get("children")!=null){

          val dataEntity=row.get(validationEntity.get("name")).asInstanceOf[util.HashMap[String,AnyVal]]

          val itValidationElements=validationEntity.get("children").asInstanceOf[util.ArrayList[AnyVal]].iterator
          while(itValidationElements.hasNext){
            val validationElement=itValidationElements.next().asInstanceOf[util.HashMap[String,AnyVal]]

            val dataElement:String=dataEntity.get(validationElement.get("name")).asInstanceOf[String]

            if(validationElement.get("regex")!=null
              && !validationElement.get("regex").asInstanceOf[String].equalsIgnoreCase("")){
              //TODO - RegEx

              if(!dataElement.matches(validationElement.get("regex").asInstanceOf[String])){
                println("Regex Validation failed : ["+ dataEntity.toString+"] ["+dataElement+"] ["+validationElement.get("regex")+"]")

                flag=false

              }
            }
          }
        }else{
          //TODO - Validation
        }
      }

      flag
    }).asInstanceOf[RDD[AnyRef]]

    /*outputRDD.collect().foreach(println)

    outputRDD

    //outputRDD.collect.foreach(println)*/
  }

}
