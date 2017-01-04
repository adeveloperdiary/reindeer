package com.metlife.santa.core.bean

import java.util

import scala.beans.BeanProperty

case class DonnerConfig() {
  @BeanProperty var mapping: util.ArrayList[Entity]=null
  @BeanProperty var lookups: util.ArrayList[LookUp]=null
}

case class Entity(){
  @BeanProperty var entityName: String = null
  @BeanProperty var sorEntityName: String = null
  @BeanProperty var attributeMap: util.ArrayList[Attribute] = null
}

case class Attribute(){
  @BeanProperty var name: String = null
  @BeanProperty var transform: String = null
  @BeanProperty var source: String = null
  @BeanProperty var dtype: String = null

}

case class LookUp(){
  @BeanProperty var table: String = null
  @BeanProperty var name: String = null

}
