import com.metlife.santa.core._
import org.apache.log4j.{Level, Logger}

object Job {

  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)

    val dasher: Dasher = new Dasher
    dasher.init("dasher").process()

    val dancer: Dancer = new Dancer
    dancer.init("dancer").chain(dasher).process()

    val vixen: Vixen = new Vixen
    vixen.init("vixen").chain(dancer).process()

    val donner: Donner = new Donner
    donner.init("donner").chain(vixen).process()

    val comet:Comet=new Comet
    comet.init("comet").chain(donner).process()

    val cupid:Cupid=new Cupid
    cupid.init("cupid").chain(donner).process()


  }
}
