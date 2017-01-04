package com.metlife.santa.core.bean

import java.util

import scala.beans.BeanProperty

case class SantaConfig() {
  @BeanProperty var jobs: util.ArrayList[Job]=null
}

case class Job(){
  @BeanProperty var name: String = null
  @BeanProperty var chain: String = null

}