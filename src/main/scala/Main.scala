import com.metlife.santa.core.{Dancer, Dasher, Vixen}

object Job {

  def main(args: Array[String]) {
    val j: Dasher = new Dasher
    j.init("santa.dasher.config.json")

    val d: Dancer = new Dancer
    d.init("santa.dancer.properties")


    val s: Vixen = new Vixen
    s.init("santa.vixen.config.json")

    s.process(d.process(j.process,j.getSparkContext()),j.getSparkContext())

  }
}
