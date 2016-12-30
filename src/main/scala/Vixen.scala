import java.util

import com.metlife.santa.config.VixenConfig
import org.apache.spark.rdd.RDD

class Vixen extends ReindeerBase{

  def init(file: String) = {
    initReindeer(classOf[VixenConfig],file)
  }

  def process(inputRDD:RDD[util.Map[String, AnyRef]]) = {
    inputRDD.collect.foreach(println)
  }
}
