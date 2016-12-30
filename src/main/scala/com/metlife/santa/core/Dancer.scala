package com.metlife.santa.core

import java.io.{File, FileInputStream}
import java.util
import net.sf.cb2java.copybook.{Copybook, CopybookParser}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

class Dancer extends ReindeerBase{

  def init(file: String) = {
    initReindeer(file)
  }

  def process(inputRDD:RDD[String],sc:SparkContext):RDD[util.Map[String, AnyRef]] = {
    val file: String = this.prop.getProperty("copybook_url")

    val copybook=sc.broadcast[Copybook](CopybookParser.parse("A", new FileInputStream(new File(file))))

    /*inputRDD.foreachPartition{records =>
      records.map(record =>
        copybook.value.parseData(record.getBytes).toMap()
      )
    }*/

    inputRDD.map(line=>{
      val record=copybook.value.parseData(line.getBytes())
      record.toMap()

    })
  }
}
