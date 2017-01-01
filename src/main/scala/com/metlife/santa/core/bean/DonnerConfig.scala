package com.metlife.santa.core.bean



import java.util

import scala.beans.BeanProperty

case class DonnerConfig() {
  @BeanProperty var mapping: util.ArrayList[Entity]=null
}

case class Entity(){
  @BeanProperty var entityName: String = null
  @BeanProperty var sorEntityName: String = null
  @BeanProperty var lookups: String = null
  @BeanProperty var attributeMap: util.ArrayList[Attribute] = null
}

case class Attribute(){
  @BeanProperty var name: String = null
  @BeanProperty var transform: String = null
  @BeanProperty var source: String = null

}
