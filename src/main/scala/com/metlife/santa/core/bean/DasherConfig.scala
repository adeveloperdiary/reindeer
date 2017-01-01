package com.metlife.santa.core.bean

import scala.beans.BeanProperty

case class DasherConfig() {
  @BeanProperty var name: String = null
  @BeanProperty var master: String = null
  @BeanProperty var input_url: String = null
}
