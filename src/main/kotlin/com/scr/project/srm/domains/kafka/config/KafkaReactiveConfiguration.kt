package com.scr.project.srm.domains.kafka.config

import com.scr.project.srm.RewardedKafkaDto
import com.scr.project.srm.config.TopicProperties
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

@Configuration
@EnableConfigurationProperties(TopicProperties::class)
class KafkaReactiveConfiguration(
    @Value("\${spring.kafka.bootstrap-servers}") private val bootstrapServers: String,
    @Value("\${spring.kafka.consumer.group-id}") private val groupId: String,
    @Value("\${spring.kafka.properties.schema.registry.url}") private val schemaRegistryUrl: String,
    @Value("\${spring.kafka.security-protocol}") private val securityProtocol: String,
    @Value("\${spring.kafka.sasl.mechanism}") private val saslMechanism: String,
    @Value("\${spring.kafka.sasl.username}") private val saslUsername: String?,
    @Value("\${spring.kafka.sasl.password}") private val saslPassword: String?,
    private val topicProperties: TopicProperties,
) {

    private val receiverProperties = mapOf(
        BOOTSTRAP_SERVERS_CONFIG to bootstrapServers,
        GROUP_ID_CONFIG to groupId,
        KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
        VALUE_DESERIALIZER_CLASS_CONFIG to KafkaAvroDeserializer::class.java.name,
        "schema.registry.url" to schemaRegistryUrl,
        SPECIFIC_AVRO_READER_CONFIG to true,
        "security.protocol" to securityProtocol,
        "sasl.mechanism" to saslMechanism,
        "sasl.jaas.config" to "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"$saslUsername\" password=\"$saslPassword\";",
    )

    @Bean
    fun rewardedReceiver(): KafkaReceiver<String, RewardedKafkaDto> {
        val receiverOptions = ReceiverOptions.create<String, RewardedKafkaDto>(receiverProperties)
            .subscription(listOf(topicProperties.rewardedCreationNotification))
        return KafkaReceiver.create(receiverOptions)
    }
}