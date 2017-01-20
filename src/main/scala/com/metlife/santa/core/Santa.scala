package com.metlife.santa.core

import com.metlife.santa.core.bean.{Job, SantaConfig}
import java.io.{IOException, InputStream}

import org.apache.log4j.{Level, Logger}
import org.codehaus.jackson.map.ObjectMapper

class Santa {

  private var config: SantaConfig = null
  private val base="com.metlife.santa.core."

  @throws[IOException]
  def readConfig(file: String) {
    val fileName="santa."+file+".config.json"
    val mapper = new ObjectMapper
    var in: InputStream = null
    try {
      in = getClass.getClassLoader.getResourceAsStream(fileName)
      config = mapper.readValue(in, classOf[SantaConfig])
    } finally {
      in.close
    }
  }

  def init(file: String): Unit = {
    readConfig("job")
  }

  def process(): Unit = {
    println("config.getJobs.toString"+config.getJobs.toString)

    val obj=collection.mutable.Map.empty[String,Reindeer]

    config.jobs.toArray().foreach(j=>{
      val job=j.asInstanceOf[Job]
      println("Job name :" +job.name+"----Chain Name"+job.chain)

      val jobInstance:ReindeerBase = actionBuilder(job.name)
      if(job.chain.equalsIgnoreCase("")){
        jobInstance.init(job.name.toLowerCase).process()
      }else{
        jobInstance.init(job.name.toLowerCase).chain(obj.get(job.chain).get).process()
      }
      obj.put(job.name,jobInstance)

    })
  }

  def actionBuilder(name: String): ReindeerBase = {
    val action = Class.forName(base+name).newInstance()
    action.asInstanceOf[ReindeerBase]
  }
}

object Santa{
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("RexsterClientFactory").setLevel(Level.ERROR)

    val santa:Santa=new Santa()
    santa.init("job")
    santa.process()
  }
}