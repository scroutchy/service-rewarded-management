package com.scr.project.srm.domains.kafka.config

import com.scr.project.srm.RewardedKafkaDto
import com.scr.project.srm.config.TopicProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions

@Configuration
@EnableConfigurationProperties(TopicProperties::class)
class KafkaReactiveConfiguration(
    private val kafkaAvroConsumerProperties: Map<String, Any>,
    private val topicProperties: TopicProperties,
) {

    @Bean
    fun rewardedReceiver(): KafkaReceiver<String, RewardedKafkaDto> {
        val receiverOptions = ReceiverOptions.create<String, RewardedKafkaDto>(kafkaAvroConsumerProperties)
            .subscription(listOf(topicProperties.rewardedCreationNotification))
        return KafkaReceiver.create(receiverOptions)
    }
}