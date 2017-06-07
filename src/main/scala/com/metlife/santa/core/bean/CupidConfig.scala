package com.metlife.santa.core.bean

import java.util

import scala.beans.BeanProperty

case class CupidConfig() {
  @BeanProperty var config: Config = null
  @BeanProperty var graph: Graph=null
}

case class Graph(){
  @BeanProperty var entity_name: String = null
  @BeanProperty var keys: util.ArrayList[VAttribute] = null
  @BeanProperty var attributes: util.ArrayList[VAttribute]=null
  @BeanProperty var relationships: util.ArrayList[Relationships]=null
}

case class Config(){
  @BeanProperty var url: String = null
}

case class VAttribute(){
  @BeanProperty var name: String = null
  @BeanProperty var dtype: String = null
}

case class Relationships() {
  @BeanProperty var related_entity: RelatedEntity = null
  @BeanProperty var relationship: Relationship = null
}

case class RelatedEntity(){
  @BeanProperty var name: String = null
  @BeanProperty var dirty: Boolean = false
  @BeanProperty var keys: util.ArrayList[VAttribute] = null
  @BeanProperty var attributes: util.ArrayList[VAttribute]=null
}

case class Relationship(){
  @BeanProperty var name: String = null
  @BeanProperty var attributes: util.ArrayList[VAttribute]=null
}