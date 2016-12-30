import com.metlife.santa.config.DasherConfig
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

class Dasher extends ReindeerBase{

  private var conf: SparkConf = null
  private var sc: SparkContext = null

  def init(file: String) = {
    initReindeer(classOf[DasherConfig],file)
  }

  def process():RDD[String] = {

    val objDasherConfig: DasherConfig = config.asInstanceOf[DasherConfig]

    this.conf = new SparkConf().setAppName(objDasherConfig.getName).setMaster(objDasherConfig.getMaster)
    this.sc = new SparkContext(conf)

    this.sc.textFile(objDasherConfig.getInput_url)

  }
}