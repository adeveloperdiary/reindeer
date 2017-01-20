package com.metlife.santa.core.bean

import java.util

import scala.beans.BeanProperty

case class CupidConfig() {
  @BeanProperty var config: Config = null
  @BeanProperty var vertex: util.ArrayList[Vertex]=null
  @BeanProperty var edge: util.ArrayList[Edge]=null
}

case class Vertex() {
  @BeanProperty var label: String = null
  @BeanProperty var keys: util.ArrayList[VAttribute] = null
  @BeanProperty var property: util.ArrayList[VAttribute]=null
}

case class Config(){
  @BeanProperty var url: String = null
}

case class VAttribute(){
  @BeanProperty var name: String = null
  @BeanProperty var dtype: String = null
}

case class Edge() {
  @BeanProperty var parent: String = null
  @BeanProperty var relationship: String = null
  @BeanProperty var child: String = null
}