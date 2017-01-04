package com.metlife.santa.core

import com.metlife.santa.core.bean.SantaConfig


class Santa extends ReindeerBase{
  override def init(file: String): ReindeerBase = {
    initReindeer(classOf[SantaConfig],file)
    this
  }

  override def process(): Unit = {

    println(reindeerConfig.asInstanceOf[SantaConfig].getJobs.toString)

  }
}


object Santa{
  def main(args: Array[String]) {

    val santa:Santa=new Santa()
    santa.init("job").process()

  }
}