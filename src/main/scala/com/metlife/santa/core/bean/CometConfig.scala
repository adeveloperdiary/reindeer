package com.metlife.santa.core.bean

import java.util

import scala.beans.BeanProperty


case class CometConfig() {
  @BeanProperty var mapping: util.ArrayList[Tables]=null
}

case class Tables(){
  @BeanProperty var tableName: String = null
  @BeanProperty var entityName: String = null
}