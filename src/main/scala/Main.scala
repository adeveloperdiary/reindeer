import com.metlife.santa.core.{Dancer, Dasher, Donner, Vixen}

object Job {

  def main(args: Array[String]) {
    val dasher: Dasher = new Dasher
    dasher.init("dasher")
    dasher.process()

    val dancer: Dancer = new Dancer
    dancer.init("dancer").chain(dasher)
    dancer.process()

    val vixen: Vixen = new Vixen
    vixen.init("vixen").chain(dancer)
    vixen.process()

    val donner: Donner = new Donner
    donner.init("donner").chain(vixen)
    donner.process()
  }
}
