package com.li.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.config.SaslConfigs

object LocalProducer {

//  private val BROKER_LIST ="192.168.65.132:9092"
  private val BROKER_LIST = "172.20.119.175:9092,172.20.47.150:9092,172.20.101.187:9092"
  private val TARGET_TOPIC = Array("bd_canal_order_tbl_order_master")

  private val props = new Properties()
  props.put("bootstrap.servers", BROKER_LIST)
  props.put("acks", "all")
  props.put("retries", "0")
  props.put("batch.size", "16384")
  props.put("linger.ms", "100")
  props.put("buffer.memory", "33554432")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  props.put("security.protocol", "SASL_PLAINTEXT")
  props.put("sasl.mechanism", "PLAIN")
  props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"admin\" password=\"admin\";")

  def main(args: Array[String]) {

    val producer = new KafkaProducer[String, String](props)

    val s = System.currentTimeMillis()
    for (i <- 1 to 100000) {
      val message = new ProducerRecord[String, String](this.TARGET_TOPIC(0), "{\"id\":1" + (i * i) + ",\"n2ame\":\"stu_" + i + "\",\"score\":92.02}")
      producer.send(message)
      Thread.sleep(1000L)
      println(i)
    }

    println("------------------------------------------------------------------------------------")
    val e = System.currentTimeMillis()
    println(e - s)
    producer.close()
  }
}
