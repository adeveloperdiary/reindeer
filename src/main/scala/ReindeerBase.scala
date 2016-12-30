import java.io.{ IOException, InputStream}
import java.util.Properties
import org.codehaus.jackson.map.ObjectMapper


class ReindeerBase {

  protected var prop: Properties = new Properties()
  protected var config: Any = null

  @throws[IOException]
  def initReindeer(file: String) {
    this.prop.load(this.getClass.getClassLoader.getResourceAsStream(file))
  }

  @throws[IOException]
  def initReindeer(className: Class[_], file: String) {
    val obj: ObjectMapper = new ObjectMapper
    var in: InputStream = null
    try {
      in = this.getClass.getClassLoader.getResourceAsStream(file)
      this.config = obj.readValue(in, className)
    } finally {
      in.close
    }
  }

}



