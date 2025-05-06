package com.scr.project.srm

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestKafkaConfig {

    @Bean
    fun testRewardedProducer(
        @Value("\${spring.kafka.bootstrap-servers}") bootstrapServers: String,
        @Value("\${spring.kafka.schema.registry.url}") schemaRegistryUrl: String
    ) = KafkaTestProducer<RewardedKafkaDto>(bootstrapServers, schemaRegistryUrl)
}