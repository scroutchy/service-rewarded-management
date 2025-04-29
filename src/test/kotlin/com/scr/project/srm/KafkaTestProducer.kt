package com.scr.project.srm

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringSerializer
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import reactor.kotlin.core.publisher.toMono
import java.util.Properties

class KafkaTestProducer<V>(bootstrapServers: String, schemaRegistryUrl: String) {

    private val producer: KafkaSender<String, V>

    init {
        val props = Properties().apply {
            put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java.name)
            put("schema.registry.url", schemaRegistryUrl)
        }
        val senderOptions = SenderOptions.create<String, V>(props)
        producer = KafkaSender.create(senderOptions)
    }

    fun sendMessage(topic: String, key: String, value: V): Mono<SenderResult<Nothing?>> {
        val record = SenderRecord.create(topic, null, null, key, value, null)
        return producer.send(record.toMono()).next()
    }
}