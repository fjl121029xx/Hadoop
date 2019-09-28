package com.li.flink.kafka.tools.scala

import java.util.Properties

import com.li.flink.kafka.utils.RedisUtils
import org.apache.kafka.clients.consumer.{KafkaConsumer, OffsetAndMetadata}
import org.apache.kafka.common.TopicPartition
import redis.clients.jedis.Jedis

object OffSetTools {
  var offsetClient: KafkaConsumer[Array[Byte], Array[Byte]] = null
  var standardProps: Properties = null

  def init(): Properties = {
    standardProps = new Properties
    standardProps.setProperty("bootstrap.servers", "")
    standardProps.setProperty("zookeeper.connect", "")
    standardProps.setProperty("group.id", "")
    standardProps.setProperty("enable.auto.commit", "false") //TODO
    standardProps.setProperty("auto.commit.interval.ms", "5000")
    standardProps.setProperty("auto.offset.reset", "latest")
    standardProps.put("key.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer");
    standardProps.put("value.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer");
    standardProps
  }

  def getZkUtils(): Jedis = {
    RedisUtils.getJedis
  }


  def offsetHandler() = {
    val props = new Properties
    props.putAll(standardProps)
    props.setProperty("key.deserializer",
      "org.apache.kafka.common.serialization.ByteArrayDeserializer")
    props.setProperty("value.deserializer",
      "org.apache.kafka.common.serialization.ByteArrayDeserializer")

    offsetClient = new KafkaConsumer[Array[Byte], Array[Byte]](props)
  }

  def getCommittedOffset(topicName: String, partition: Int): Long = {
    init()
    offsetHandler()
    val committed = offsetClient.committed(new TopicPartition(topicName,
      partition))
    println(topicName, partition, committed.offset())
    if (committed != null) {
      committed.offset
    } else {
      0L
    }
  }

  def setCommittedOffset(topicName: String, partition: Int, offset: Long) {
    init()
    offsetHandler()
    var partitionAndOffset: java.util.Map[TopicPartition, OffsetAndMetadata] =
      new java.util.HashMap[TopicPartition, OffsetAndMetadata]()
    partitionAndOffset.put(new TopicPartition(topicName, partition), new
        OffsetAndMetadata(offset))
    offsetClient.commitSync(partitionAndOffset)
  }

  def close() {
    offsetClient.close()
  }
}
