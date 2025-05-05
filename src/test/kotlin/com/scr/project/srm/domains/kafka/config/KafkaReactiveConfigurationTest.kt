package com.scr.project.srm.domains.kafka.config

import com.scr.project.commons.cinema.kafka.config.KafkaAvroConsumerConfiguration
import com.scr.project.srm.config.TopicProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KafkaReactiveConfigurationTest {

    private val topicProperties = TopicProperties()
    private val consumerConfiguration =
        KafkaAvroConsumerConfiguration("bootstrapServers", "schemaRegistryUrl", "PLAINTEXT", "", "groupId", null, null)

    private val config = KafkaReactiveConfiguration(
        consumerConfiguration.kafkaAvroConsumerProperties(),
        topicProperties
    )

    @Test
    fun `rewardedReceiver should succeed`() {
        val receiver = config.rewardedReceiver()
        assertThat(receiver).isNotNull
    }
}