package com.metlife.santa.core

import java.io.{File, FileInputStream}
import net.sf.cb2java.copybook.{Copybook, CopybookParser}
import org.apache.spark.rdd.RDD

class Dancer extends ReindeerBase{

  override def init(file: String):ReindeerBase = {
    initReindeer(file)
    this
  }

  override def process() = {

    println("")
    println("=============== DANCER ( COPYBOOK 2 MAP ) ===================")
    println("")

    val _tempRDD=inputRDD.asInstanceOf[RDD[String]]

    val file: String = this.prop.getProperty("copybook_url")


    spark.sparkContext.textFile("/Users/abhisekjana/spark/reindeer/src/main/resources/a.input.txt").collect().foreach(println)



    val copybook=spark.sparkContext.broadcast[Copybook](CopybookParser.parse("A", new FileInputStream(new File(file))))



    /*inputRDD.foreachPartition{records =>
      records.map(record =>
        copybook.value.parseData(record.getBytes).toMap()
      )
    }*/

    outputRDD=_tempRDD.map(line=>{
      val record=copybook.value.parseData(line.getBytes())
      record.toMap()

    })

    //outputRDD.collect().foreach(println)

  }
}
