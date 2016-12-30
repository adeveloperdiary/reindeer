import java.io.{File, FileInputStream}
import java.util

import net.sf.cb2java.copybook.{Copybook, CopybookParser}
import org.apache.spark.rdd.RDD

class Dancer extends ReindeerBase{

  def init(file: String) = {
    initReindeer(file)
  }

  def process(inputRDD:RDD[String]):RDD[util.Map[String, AnyRef]] = {
    val file: String = this.prop.getProperty("copybook_url")


    /*inputRDD.foreachPartition{records =>
      val copybook: Copybook = CopybookParser.parse("A", this.getClass.getClassLoader.getResourceAsStream(file))
      records.foreach(record =>
        return copybook.parseData(record.getBytes).toMap()
      )
    }*/

    inputRDD.map(line=>{
      val copybook: Copybook = CopybookParser.parse("A", new FileInputStream(new File(file)))
      val record=copybook.parseData(line.getBytes())
      record.toMap()

    })

  }
}