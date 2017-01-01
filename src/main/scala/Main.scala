import com.metlife.santa.core._

object Job {

  def main(args: Array[String]) {
    val dasher: Dasher = new Dasher
    dasher.init("dasher").process()

    val dancer: Dancer = new Dancer
    dancer.init("dancer").chain(dasher).process()

    val vixen: Vixen = new Vixen
    vixen.init("vixen").chain(dancer).process()

    val donner: Donner = new Donner
    donner.init("donner").chain(vixen).process()

    //val commet:Comet=new Comet
    //commet.init("commet").chain(donner).process()

  }
}
